package com.myproject.clinic.rag.service;

import com.myproject.clinic.entity.Doctor;
import com.myproject.clinic.entity.HealthPackage;
import com.myproject.clinic.entity.Specialization;
import com.myproject.clinic.rag.dto.ChatRequest;
import com.myproject.clinic.rag.dto.ChatResponse;
import com.myproject.clinic.rag.dto.ExtractionResult;
import com.myproject.clinic.repository.DoctorRepository;
import com.myproject.clinic.repository.HealthPackageRepository;
import com.myproject.clinic.repository.SpecializationRepository;
import com.myproject.clinic.utils.EmbeddingService;
import com.myproject.clinic.exception.ChatSessionPersistenceException;
import com.myproject.clinic.utils.ChatSessionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Chatbot Service — điều phối luồng xử lý theo Strategy + Factory Pattern.
 *
 * Luồng:
 *   ChatRequest
 *     -> IntentClassifier  (phân loại intent)
 *     -> IntentStrategyFactory (chọn đúng strategy — O(1), không if/else)
 *     -> ChatbotIntentStrategy.handle() (xử lý logic)
 *     -> ChatResponse
 *
 * ChatbotService KHÔNG chứa bất kỳ if/else lớn nào cho từng intent.
 * Thêm intent mới = chỉ cần tạo class mới implement ChatbotIntentStrategy.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatbotService {

    private final IntentClassifier intentClassifier;
    private final IntentStrategyFactory intentStrategyFactory;
    private final DataExtractionService dataExtractionService;
    private final ChatSessionStore chatSessionStore;

    // Dùng cho reindexAll() — không liên quan đến luồng chat
    private final EmbeddingService embeddingService;
    private final SpecializationRepository specializationRepository;
    private final DoctorRepository doctorRepository;
    private final HealthPackageRepository healthPackageRepository;

    /**
     * Xử lý tin nhắn của người dùng.
     * Sử dụng Factory để resolve strategy, không có if/else theo intent.
     */
    public ChatResponse processMessage(ChatRequest request) {
        try {
            String sessionId = request.getSessionId();
            String message   = request.getMessage();
            Long   userId    = request.getUserId();

            // 1. Load hoặc khởi tạo session state
            Map<String, Object> state = chatSessionStore.getState(sessionId);
            if (state == null) {
                state = new HashMap<>();
                state.put("chatHistory", new ArrayList<>());
            }

            // 2. Ghi tin nhắn người dùng vào lịch sử (giới hạn 10 tin)
            @SuppressWarnings("unchecked")
            List<Map<String, String>> chatHistory =
                    (List<Map<String, String>>) state.getOrDefault("chatHistory", new ArrayList<>());
            chatHistory.add(Map.of("role", "user", "content", message));
            if (chatHistory.size() > 10) {
                chatHistory = new ArrayList<>(chatHistory.subList(chatHistory.size() - 10, chatHistory.size()));
            }
            state.put("chatHistory", chatHistory);

            // 3. Phân loại intent — Tầng 1 Regex (nhanh), Tầng 2 LLM (fallback)
            List<String> intents = intentClassifier.classify(message);
            String primaryIntent = intents.isEmpty() ? "UNKNOWN" : intents.get(0);
            log.info("[ChatbotService] Intent='{}' | message='{}'",
                    primaryIntent, message.substring(0, Math.min(50, message.length())));

            // 4. Chuẩn bị state cho STATISTICS (cần extractedParams trước khi vào strategy)
            if ("STATISTICS".equals(primaryIntent)) {
                ExtractionResult params = dataExtractionService.extract(message, intents, chatHistory);
                state.put("extractedParams", params);
            }
            state.put("intent", primaryIntent);

            // 5. Factory chọn Strategy — O(1), không if/else
            ChatbotIntentStrategy strategy = intentStrategyFactory.getStrategy(primaryIntent);
            ChatResponse response = strategy.handle(request, state);

            // 6. Lưu response vào lịch sử và persist state
            chatHistory.add(Map.of("role", "assistant", "content", response.getMessage()));
            state.put("chatHistory", chatHistory);
            chatSessionStore.saveState(sessionId, userId, state);

            return response;

        } catch (ChatSessionPersistenceException e) {
            log.error("[ChatbotService] Lỗi lưu phiên chat: {}", e.getMessage(), e);
            return errorResponse("Xin lỗi, hệ thống không thể lưu phiên hội thoại. Vui lòng thử lại.");
        } catch (Exception e) {
            log.error("[ChatbotService] Lỗi nghiêm trọng: {}", e.getMessage(), e);
            return errorResponse("Xin lỗi, hệ thống chatbot đang gặp sự cố kỹ thuật tạm thời. Vui lòng thử lại sau giây lát.");
        }
    }

    /**
     * Hàm tiện ích (Utility) để tạo một phản hồi lỗi mặc định.
     * Khi có lỗi ngoại lệ không mong muốn xảy ra, trả về DTO này để hiển thị trên UI.
     *
     * @param message Thông báo lỗi hiển thị cho người dùng
     * @return ChatResponse với trạng thái ERROR
     */
    private ChatResponse errorResponse(String message) {
        return ChatResponse.builder()
                .message(message)
                .intent("ERROR")
                .step("DONE")
                .build();
    }

    // =====================================================================
    // Reindex embeddings — không liên quan đến luồng chat
    // =====================================================================

    /**
     * Hàm thực thi quá trình tái tạo lại Vector (Embedding) cho tất cả dữ liệu
     * (Chuyên khoa, Bác sĩ, Gói khám) trong Database.
     * Được gọi bởi Admin khi có thay đổi lớn về dữ liệu để Semantic Search hoạt động chính xác.
     * Lưu ý: Không liên quan đến luồng chat real-time của người dùng.
     */
    public void reindexAll() {
        log.info("Starting reindex of all embeddings...");

        List<Specialization> specs = specializationRepository.findAll();
        for (Specialization spec : specs) {
            String text = spec.getName() + " " + (spec.getDescription() != null ? spec.getDescription() : "");
            List<Double> embedding = embeddingService.getEmbedding(text);
            spec.setEmbedding(embeddingService.embeddingToJson(embedding));
            specializationRepository.save(spec);
            log.info("Reindexed specialization: {}", spec.getName());
        }

        List<Doctor> doctors = doctorRepository.findAll();
        for (Doctor doctor : doctors) {
            String text = buildDoctorEmbeddingText(doctor);
            List<Double> embedding = embeddingService.getEmbedding(text);
            doctor.setEmbedding(embeddingService.embeddingToJson(embedding));
            doctorRepository.save(doctor);
            log.info("Reindexed doctor: {}", doctor.getUser().getFullName());
        }

        List<HealthPackage> packages = healthPackageRepository.findAll();
        for (HealthPackage hp : packages) {
            String text = hp.getName() + " " + (hp.getDescription() != null ? hp.getDescription() : "");
            List<Double> embedding = embeddingService.getEmbedding(text);
            hp.setEmbedding(embeddingService.embeddingToJson(embedding));
            healthPackageRepository.save(hp);
            log.info("Reindexed health package: {}", hp.getName());
        }

        log.info("Reindex completed.");
    }

    /**
     * Hàm nội bộ để ghép các thông tin của bác sĩ thành một đoạn văn bản (Text block).
     * Đoạn văn bản này sau đó sẽ được mang đi tạo Embedding (Vector).
     *
     * @param doctor Thực thể Bác sĩ
     * @return Đoạn văn bản mô tả bác sĩ
     */
    private String buildDoctorEmbeddingText(Doctor doctor) {
        StringBuilder sb = new StringBuilder();
        sb.append(doctor.getUser().getFullName()).append(" ");
        if (doctor.getUser().getGender() != null) sb.append(doctor.getUser().getGender()).append(" ");
        if (doctor.getUser().getAddress() != null) sb.append(doctor.getUser().getAddress()).append(" ");
        if (doctor.getSpecialization() != null) sb.append(doctor.getSpecialization().getName()).append(" ");
        if (doctor.getBio() != null) sb.append(doctor.getBio());
        return sb.toString().trim();
    }
}
