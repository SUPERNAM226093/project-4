package com.myproject.clinic.rag.service;

import com.myproject.clinic.entity.Specialization;
import com.myproject.clinic.rag.dto.ChatRequest;
import com.myproject.clinic.rag.dto.ChatResponse;
import com.myproject.clinic.repository.SpecializationRepository;
import com.myproject.clinic.utils.EmbeddingService;
import com.myproject.clinic.utils.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Strategy xử lý intent SYMPTOM: người dùng mô tả triệu chứng và hệ thống gợi ý chuyên khoa phù hợp.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SymptomIntentHandler implements ChatbotIntentStrategy {

    private final SpecializationRepository specializationRepository;
    private final EmbeddingService embeddingService;
    private final LlmService llmService;

    @Override
    public String getSupportedIntent() {
        return "SYMPTOM";
    }

    @Override
    public ChatResponse handle(ChatRequest request, java.util.Map<String, Object> state) {
        return handle(request.getMessage(), state);
    }

    /**
     * Phân tích triệu chứng, yêu cầu LLM trả về tag suggestions để backend gắn đúng card chuyên khoa.
     */
    public ChatResponse handle(String message, java.util.Map<String, Object> state) {
        List<Double> queryEmbedding = embeddingService.getEmbedding(message);
        List<Specialization> allSpecs = specializationRepository.findAll();
        boolean hasQueryEmbedding = !queryEmbedding.isEmpty();

        List<Map.Entry<Specialization, Double>> scored = new ArrayList<>();
        for (Specialization spec : allSpecs) {
            List<Double> specEmbedding = embeddingService.jsonToEmbedding(spec.getEmbedding());
            double similarity;
            if (hasQueryEmbedding && !specEmbedding.isEmpty()) {
                similarity = embeddingService.cosineSimilarity(queryEmbedding, specEmbedding);
            } else {
                similarity = calculateKeywordSimilarity(message, spec);
            }
            if (similarity > 0.0) {
                scored.add(Map.entry(spec, similarity));
            }
        }
        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Specialization> topSpecs = scored.stream()
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        if (topSpecs.isEmpty()) {
            topSpecs = allSpecs.stream().limit(3).toList();
        }

        if (topSpecs.isEmpty()) {
            return ChatResponse.builder()
                    .message("Xin lỗi, tôi không tìm thấy chuyên khoa phù hợp với triệu chứng bạn mô tả. Vui lòng mô tả chi tiết hơn.")
                    .intent("SYMPTOM")
                    .step("ANALYZE")
                    .build();
        }

        String allSpecNames = allSpecs.stream()
                .map(Specialization::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        List<LlmService.ChatMessage> llmMessages = new ArrayList<>();
        llmMessages.add(new LlmService.ChatMessage("system",
                "Bạn là trợ lý y tế. Người dùng mô tả triệu chứng, bạn hãy:\n" +
                        "1. Phân tích ngắn gọn triệu chứng (2-3 câu)\n" +
                        "2. Gợi ý 1 đến 3 chuyên khoa liên quan nhất từ danh sách sau: " + allSpecNames + "\n" +
                        "3. Lưu ý đây chỉ là gợi ý sơ bộ, cần được bác sĩ thăm khám trực tiếp.\n" +
                        "Trả lời bằng tiếng Việt, ngắn gọn. Hãy viết đúng tên chuyên khoa có trong danh sách.\n\n" +
                        "BẮT BUỘC ở cuối câu trả lời, thêm phần gợi ý chuyên khoa trong tag XML:\n" +
                        "<suggestions>[Tên Chuyên Khoa 1, Tên Chuyên Khoa 2]</suggestions>\n" +
                        "Chỉ dùng tên chuyên khoa có trong danh sách."));
        llmMessages.add(new LlmService.ChatMessage("user", message));

        String analysis = llmService.chat(llmMessages);

        if (LlmService.LLM_ERROR_SENTINEL.equals(analysis)) {
            log.warn("[SymptomIntentHandler] LLM không phản hồi được, dừng xử lý để tránh gợi ý sai.");
            return ChatResponse.builder()
                    .message("Xin lỗi, hệ thống tư vấn AI đang tạm thời quá tải. Vui lòng thử lại sau ít phút hoặc liên hệ trực tiếp phòng khám.")
                    .intent("SYMPTOM")
                    .step("ERROR")
                    .build();
        }

        String userMessage = analysis;
        int suggestStart = analysis.indexOf("<suggestions>");
        int suggestEnd = analysis.indexOf("</suggestions>");

        List<Specialization> finalSpecs = new ArrayList<>();

        if (suggestStart != -1 && suggestEnd != -1 && suggestEnd > suggestStart) {
            userMessage = (analysis.substring(0, suggestStart).trim()
                    + "\n"
                    + analysis.substring(suggestEnd + "</suggestions>".length()).trim()).trim();

            String content = analysis.substring(suggestStart + "<suggestions>".length(), suggestEnd);
            content = content.replace("[", "").replace("]", "");
            String[] parts = content.split(",");

            for (String part : parts) {
                String specNameClean = AliasNormalizationService.removeDiacritics(part.trim().toLowerCase());
                for (Specialization spec : allSpecs) {
                    String cleanName = AliasNormalizationService.removeDiacritics(spec.getName().toLowerCase());
                    if (cleanName.equals(specNameClean) && !finalSpecs.contains(spec)) {
                        finalSpecs.add(spec);
                    }
                }
            }
        }

        if (finalSpecs.isEmpty()) {
            String lowerMessage = userMessage.toLowerCase();
            int idx = lowerMessage.indexOf("gợi ý các chuyên khoa");
            if (idx == -1) idx = lowerMessage.indexOf("gợi ý chuyên khoa");
            if (idx == -1) idx = lowerMessage.indexOf("chuyên khoa gợi ý");
            if (idx == -1) idx = AliasNormalizationService.removeDiacritics(lowerMessage).indexOf("chuyen khoa goi y");
            if (idx != -1) {
                userMessage = userMessage.substring(0, idx).trim();
            }

            String textToSearch = userMessage.toLowerCase();
            String cleanTextToSearch = AliasNormalizationService.removeDiacritics(textToSearch);
            for (Specialization spec : allSpecs) {
                String specName = spec.getName().toLowerCase();
                String cleanSpecName = AliasNormalizationService.removeDiacritics(specName);
                if (specName.split("\\s+").length > 1) {
                    if (textToSearch.contains(specName) || cleanTextToSearch.contains(cleanSpecName)) {
                        finalSpecs.add(spec);
                    }
                } else if (textToSearch.contains("khoa " + specName)
                        || cleanTextToSearch.contains("khoa " + cleanSpecName)) {
                    finalSpecs.add(spec);
                }
            }
        }

        if (finalSpecs.isEmpty()) {
            log.warn("[SymptomIntentHandler] LLM không đề cập tên chuyên khoa cụ thể. Dùng similarity fallback.");
            for (Map.Entry<Specialization, Double> entry : scored) {
                if (entry.getValue() >= 0.3) {
                    finalSpecs.add(entry.getKey());
                }
            }
            if (finalSpecs.isEmpty() && !scored.isEmpty()) {
                finalSpecs.add(scored.get(0).getKey());
            }
        }

        List<ChatResponse.CardItem> specCards = finalSpecs.stream()
                .map(spec -> ChatResponse.CardItem.builder()
                        .id(spec.getId())
                        .name(spec.getName())
                        .description(spec.getDescription())
                        .featureImageUrl(spec.getFeatureImage() != null ? "/images/" + spec.getFeatureImage() : null)
                        .type("specialization")
                        .build())
                .toList();

        return ChatResponse.builder()
                .message(userMessage)
                .intent("SYMPTOM")
                .step("RESULT")
                .specializations(specCards)
                .build();
    }

    private double calculateKeywordSimilarity(String query, Specialization spec) {
        if (query == null || spec == null) return 0.0;
        String cleanQuery = AliasNormalizationService.removeDiacritics(query.toLowerCase());
        String cleanSpecName = AliasNormalizationService.removeDiacritics(spec.getName().toLowerCase());

        cleanQuery = cleanQuery.replaceAll("^(chuyen\\s+)?khoa\\s+", "").trim();
        cleanSpecName = cleanSpecName.replaceAll("^(chuyen\\s+)?khoa\\s+", "").trim();

        if (cleanQuery.contains(cleanSpecName) || cleanSpecName.contains(cleanQuery)) {
            return 1.0;
        }

        String cleanDesc = spec.getDescription() != null
                ? AliasNormalizationService.removeDiacritics(spec.getDescription().toLowerCase())
                : "";
        String[] queryWords = cleanQuery.split("\\s+");
        int matchCount = 0;
        int totalWords = 0;
        for (String word : queryWords) {
            if (word.length() < 2) continue;
            totalWords++;
            if (cleanSpecName.contains(word) || cleanDesc.contains(word)) {
                matchCount++;
            }
        }
        if (totalWords == 0) return 0.0;
        return (double) matchCount / totalWords;
    }
}
