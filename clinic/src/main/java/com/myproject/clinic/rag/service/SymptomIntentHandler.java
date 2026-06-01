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
 * Strategy xử lý khi người dùng nhập triệu chứng bệnh (SYMPTOM).
 * - Sử dụng Vector Embedding để tìm kiếm nhanh các chuyên khoa có độ tương đồng với triệu chứng.
 * - Gọi LLM để phân tích triệu chứng (dự đoán nguyên nhân sơ bộ).
 * - Yêu cầu LLM khuyên người dùng đi khám ở các chuyên khoa liên quan.
 * LƯU Ý: Luôn có khuyến cáo người dùng nên đi khám trực tiếp.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SymptomIntentHandler implements ChatbotIntentStrategy {

    private final SpecializationRepository specializationRepository;
    private final EmbeddingService embeddingService;
    private final LlmService llmService;

    // === STRATEGY PATTERN ===

    @Override
    public String getSupportedIntent() {
        return "SYMPTOM";
    }

    @Override
    public ChatResponse handle(ChatRequest request, java.util.Map<String, Object> state) {
        return handle(request.getMessage(), state);
    }

    // === LOGIC GỐC (giữ nguyên) ===

    /**
     * Xử lý chính luồng tư vấn triệu chứng:
     * 1. Vector hóa triệu chứng của user.
     * 2. Tính Cosine Similarity với Vector của tất cả chuyên khoa, lấy top 3 khoa giống nhất làm fallback.
     * 3. Gửi danh sách toàn bộ khoa + triệu chứng cho LLM phân tích.
     * 4. LLM trả về văn bản phân tích và tên các khoa gợi ý.
     * 5. Lọc ra các thẻ chuyên khoa tương ứng với gợi ý của LLM (hoặc fallback từ bước 2).
     */
    public ChatResponse handle(String message, java.util.Map<String, Object> state) {
        // Get embedding for user's symptoms
        List<Double> queryEmbedding = embeddingService.getEmbedding(message);
        List<Specialization> allSpecs = specializationRepository.findAll();

        // Score specializations by cosine similarity
        List<Map.Entry<Specialization, Double>> scored = new ArrayList<>();
        for (Specialization spec : allSpecs) {
            List<Double> specEmbedding = embeddingService.jsonToEmbedding(spec.getEmbedding());
            if (!specEmbedding.isEmpty()) {
                double similarity = embeddingService.cosineSimilarity(queryEmbedding, specEmbedding);
                scored.add(Map.entry(spec, similarity));
            }
        }
        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Take top 3 matches
        List<Specialization> topSpecs = scored.stream()
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        if (topSpecs.isEmpty()) {
            return ChatResponse.builder()
                    .message("Xin lỗi, tôi không tìm thấy chuyên khoa phù hợp với triệu chứng bạn mô tả. Vui lòng mô tả chi tiết hơn.")
                    .intent("SYMPTOM")
                    .step("ANALYZE")
                    .build();
        }

        // Provide ALL specializations to LLM since clinic spec list is usually small
        String allSpecNames = allSpecs.stream().map(Specialization::getName).reduce((a, b) -> a + ", " + b).orElse("");
        List<LlmService.ChatMessage> llmMessages = new ArrayList<>();
        llmMessages.add(new LlmService.ChatMessage("system",
                "Bạn là trợ lý y tế. Người dùng mô tả triệu chứng, bạn hãy:\n" +
                "1. Phân tích ngắn gọn triệu chứng (2-3 câu)\n" +
                "2. Gợi ý 1 đến 3 chuyên khoa liên quan nhất từ danh sách sau: " + allSpecNames + "\n" +
                "3. Lưu ý: Đây chỉ là gợi ý sơ bộ, cần được bác sĩ thăm khám trực tiếp.\n" +
                "Trả lời bằng tiếng Việt, ngắn gọn. Hãy viết hoa đúng tên chuyên khoa."));
        llmMessages.add(new LlmService.ChatMessage("user", message));

        String analysis = llmService.chat(llmMessages);

        // Nếu LLM thất bại (sentinel), trả lỗi ngay, KHÔNG fallback embedding sai
        if (LlmService.LLM_ERROR_SENTINEL.equals(analysis)) {
            log.warn("[SymptomIntentHandler] LLM không phản hồi được, dừng xử lý để tránh gợi ý sai.");
            return ChatResponse.builder()
                    .message("Xin lỗi, hệ thống tư vấn AI đang tạm thời quá tải. Vui lòng thử lại sau ít phút hoặc liên hệ trực tiếp phòng khám.")
                    .intent("SYMPTOM")
                    .step("ERROR")
                    .build();
        }

        // Lọc chuyên khoa mà LLM thực sự nhắc đến trong phân tích
        List<Specialization> finalSpecs = new ArrayList<>(allSpecs.stream()
                .filter(spec -> analysis.toLowerCase().contains(spec.getName().toLowerCase()))
                .toList());

        // Chỉ bổ sung từ embedding nếu LLM đề cập ít hơn 3 khoa (không bao giờ fallback hoàn toàn sang embedding khi LLM đã có kết quả)
        if (!finalSpecs.isEmpty()) {
            for (Specialization spec : topSpecs) {
                if (finalSpecs.size() >= 3) break;
                if (!finalSpecs.contains(spec)) {
                    finalSpecs.add(spec);
                }
            }
        } else {
            // LLM không nhắc tên khoa nào (có thể format khác) → dùng top embedding nhưng log cảnh báo
            log.warn("[SymptomIntentHandler] LLM không đề cập tên chuyên khoa cụ thể. Dùng embedding fallback.");
            finalSpecs.addAll(topSpecs.stream().limit(3).toList());
        }

        // Build specialization cards
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
                .message(analysis)
                .intent("SYMPTOM")
                .step("RESULT")
                .specializations(specCards)
                .build();
    }
}
