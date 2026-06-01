package com.myproject.clinic.rag.service;

import com.myproject.clinic.entity.Doctor;
import com.myproject.clinic.entity.HealthPackage;
import com.myproject.clinic.entity.Specialization;
import com.myproject.clinic.rag.dto.ChatRequest;
import com.myproject.clinic.rag.dto.ChatResponse;
import com.myproject.clinic.repository.DoctorRepository;
import com.myproject.clinic.repository.HealthPackageRepository;
import com.myproject.clinic.repository.SpecializationRepository;
import com.myproject.clinic.utils.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Strategy xử lý SEARCH intent.
 *
 * Tối ưu tốc độ:
 * - 3 tác vụ tìm kiếm (spec, doctor, package) chạy SONG SONG với CompletableFuture.
 * - Tổng thời gian ≈ max(tìm_spec, tìm_doctor, tìm_package) thay vì tổng cộng 3 cái.
 * - Embedding so sánh thuần CPU, không block thread pool.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SearchIntentHandler implements ChatbotIntentStrategy {

    private final SpecializationRepository specializationRepository;
    private final DoctorRepository doctorRepository;
    private final HealthPackageRepository healthPackageRepository;
    private final EmbeddingService embeddingService;

    // === STRATEGY PATTERN ===

    @Override
    public String getSupportedIntent() {
        return "SEARCH";
    }

    @Override
    public ChatResponse handle(ChatRequest request, java.util.Map<String, Object> state) {
        return handle(request.getMessage(), state);
    }

    // === LOGIC GỐC — tối ưu parallel search ===

    /**
     * Logic chính của xử lý tìm kiếm (SEARCH).
     * Băm câu hỏi của user thành Vector (Embedding) và chạy 3 tác vụ tìm kiếm song song:
     * Chuyên khoa, Bác sĩ, Gói khám sức khỏe.
     * Cuối cùng tổng hợp lại số lượng kết quả và trả về cho UI hiển thị danh sách thẻ (Cards).
     *
     * @param message Tin nhắn tìm kiếm của người dùng
     * @param state Trạng thái phiên hiện tại
     * @return Đối tượng ChatResponse chứa các mảng thẻ UI (Card arrays)
     */
    public ChatResponse handle(String message, java.util.Map<String, Object> state) {
        long start = System.currentTimeMillis();
        List<Double> queryEmbedding = embeddingService.getEmbedding(message);

        // === PARALLEL: chạy 3 search đồng thời ===
        CompletableFuture<List<ChatResponse.CardItem>> specFuture =
                CompletableFuture.supplyAsync(() -> searchSpecializations(queryEmbedding));

        CompletableFuture<List<ChatResponse.DoctorCard>> doctorFuture =
                CompletableFuture.supplyAsync(() -> searchDoctors(queryEmbedding));

        CompletableFuture<List<ChatResponse.CardItem>> packageFuture =
                CompletableFuture.supplyAsync(() -> searchHealthPackages(queryEmbedding));

        // Chờ tất cả 3 hoàn thành
        CompletableFuture.allOf(specFuture, doctorFuture, packageFuture).join();

        List<ChatResponse.CardItem> specCards = specFuture.join();
        List<ChatResponse.DoctorCard> doctorCards = doctorFuture.join();
        List<ChatResponse.CardItem> packageCards = packageFuture.join();

        log.info("[SearchIntentHandler] Parallel search done in {}ms", System.currentTimeMillis() - start);

        boolean hasResults = !specCards.isEmpty() || !doctorCards.isEmpty() || !packageCards.isEmpty();

        String responseMessage;
        if (!hasResults) {
            responseMessage = "Xin lỗi, tôi không tìm thấy kết quả phù hợp. Bạn có thể thử tìm kiếm với từ khóa khác.";
        } else {
            StringBuilder sb = new StringBuilder("Đây là kết quả tìm kiếm cho: **" + message + "**\n\n");
            if (!specCards.isEmpty()) sb.append("🏥 Tìm thấy **").append(specCards.size()).append("** chuyên khoa\n");
            if (!doctorCards.isEmpty()) sb.append("👨‍⚕️ Tìm thấy **").append(doctorCards.size()).append("** bác sĩ\n");
            if (!packageCards.isEmpty()) sb.append("📦 Tìm thấy **").append(packageCards.size()).append("** gói khám\n");
            responseMessage = sb.toString();
        }

        return ChatResponse.builder()
                .message(responseMessage)
                .intent("SEARCH")
                .step("RESULT")
                .specializations(specCards.isEmpty() ? null : specCards)
                .doctors(doctorCards.isEmpty() ? null : doctorCards)
                .healthPackages(packageCards.isEmpty() ? null : packageCards)
                .build();
    }

    /**
     * Tìm kiếm Chuyên khoa dựa vào tính toán Cosine Similarity (Độ tương đồng) giữa Vector câu hỏi và Vector dữ liệu.
     * Lọc lấy top 3 chuyên khoa có độ tương đồng > 0.3.
     */
    private List<ChatResponse.CardItem> searchSpecializations(List<Double> queryEmbedding) {
        List<Specialization> allSpecs = specializationRepository.findAll();
        List<Map.Entry<Specialization, Double>> scored = new ArrayList<>();

        for (Specialization spec : allSpecs) {
            List<Double> embedding = embeddingService.jsonToEmbedding(spec.getEmbedding());
            if (!embedding.isEmpty()) {
                double sim = embeddingService.cosineSimilarity(queryEmbedding, embedding);
                if (sim > 0.3) {
                    scored.add(Map.entry(spec, sim));
                }
            }
        }
        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return scored.stream().limit(3)
                .map(e -> ChatResponse.CardItem.builder()
                        .id(e.getKey().getId())
                        .name(e.getKey().getName())
                        .description(e.getKey().getDescription())
                        .featureImageUrl(e.getKey().getFeatureImage() != null ? "/images/" + e.getKey().getFeatureImage() : null)
                        .type("specialization")
                        .build())
                .toList();
    }

    /**
     * Tìm kiếm Bác sĩ dựa vào tính toán Cosine Similarity (Độ tương đồng) giữa Vector câu hỏi và Vector dữ liệu.
     * Lọc lấy top 3 bác sĩ có độ tương đồng > 0.3.
     */
    private List<ChatResponse.DoctorCard> searchDoctors(List<Double> queryEmbedding) {
        List<Doctor> allDoctors = doctorRepository.findAll();
        List<Map.Entry<Doctor, Double>> scored = new ArrayList<>();

        for (Doctor doctor : allDoctors) {
            List<Double> embedding = embeddingService.jsonToEmbedding(doctor.getEmbedding());
            if (!embedding.isEmpty()) {
                double sim = embeddingService.cosineSimilarity(queryEmbedding, embedding);
                if (sim > 0.3) {
                    scored.add(Map.entry(doctor, sim));
                }
            }
        }
        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return scored.stream().limit(3)
                .map(e -> {
                    Doctor d = e.getKey();
                    return ChatResponse.DoctorCard.builder()
                            .id(d.getId())
                            .fullName(d.getUser().getFullName())
                            .specializationName(d.getSpecialization() != null ? d.getSpecialization().getName() : null)
                            .bio(d.getBio())
                            .featureImageUrl(d.getFeatureImage() != null ? "/images/" + d.getFeatureImage() : null)
                            .experienceYears(d.getExperienceYears())
                            .build();
                })
                .toList();
    }

    /**
     * Tìm kiếm Gói khám sức khỏe dựa vào tính toán Cosine Similarity (Độ tương đồng).
     * Lọc lấy top 3 gói khám có độ tương đồng > 0.3.
     */
    private List<ChatResponse.CardItem> searchHealthPackages(List<Double> queryEmbedding) {
        List<HealthPackage> allPackages = healthPackageRepository.findAll();
        List<Map.Entry<HealthPackage, Double>> scored = new ArrayList<>();

        for (HealthPackage hp : allPackages) {
            List<Double> embedding = embeddingService.jsonToEmbedding(hp.getEmbedding());
            if (!embedding.isEmpty()) {
                double sim = embeddingService.cosineSimilarity(queryEmbedding, embedding);
                if (sim > 0.3) {
                    scored.add(Map.entry(hp, sim));
                }
            }
        }
        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return scored.stream().limit(3)
                .map(e -> ChatResponse.CardItem.builder()
                        .id(e.getKey().getId())
                        .name(e.getKey().getName())
                        .description(e.getKey().getDescription())
                        .featureImageUrl(e.getKey().getFeatureImage() != null ? "/images/" + e.getKey().getFeatureImage() : null)
                        .type("health_package")
                        .build())
                .toList();
    }
}
