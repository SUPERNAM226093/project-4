package com.myproject.clinic.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Lớp dịch vụ (Service) xử lý logic nghiệp vụ và dữ liệu cho thực thể Embedding.
 */
@Service
@Slf4j
public class EmbeddingService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final String EMBEDDING_URL =
            "https://router.huggingface.co/hf-inference/models/dangvantuan/vietnamese-embedding/pipeline/feature-extraction";

    /**
     * Khởi tạo WebClient gọi Hugging Face embedding model bằng token cấu hình trong backend.
     * Service này phục vụ tìm kiếm ngữ nghĩa cho bác sĩ, chuyên khoa và gói khám.
     */
    public EmbeddingService(@Value("${hf.token}") String hfToken, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .defaultHeader("Authorization", "Bearer " + hfToken)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    /**
     * Phương thức: Lấy embedding.
     */
    /**
     * Gửi văn bản lên model embedding tiếng Việt và nhận về vector số.
     * Vector này được dùng để so sánh câu hỏi người dùng với dữ liệu trong database.
     */
    public List<Double> getEmbedding(String text) {
        try {
            String responseBody = webClient.post()
                    .uri(EMBEDDING_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("inputs", text))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Handle both 1D and 2D arrays returned by different Hugging Face models
            com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode != null && rootNode.isArray() && rootNode.size() > 0) {
                if (rootNode.get(0).isArray()) {
                    // It's a 2D array [[v1, v2, ...]]
                    return objectMapper.convertValue(rootNode.get(0), new TypeReference<List<Double>>() {});
                } else if (rootNode.get(0).isNumber()) {
                    // It's a 1D array [v1, v2, ...]
                    return objectMapper.convertValue(rootNode, new TypeReference<List<Double>>() {});
                }
            }
            
            log.warn("Empty or unrecognized embedding response for text: {}", text.substring(0, Math.min(50, text.length())));
            return List.of();
        } catch (Exception e) {
            log.error("Failed to get embedding for text: {}", text.substring(0, Math.min(50, text.length())), e);
            return List.of();
        }
    }

    /**
     * Phương thức: Embedding sang json.
     */
    /**
     * Chuyển vector embedding sang JSON để lưu vào các cột embedding trong database.
     * Dùng khi reindex dữ liệu chuyên khoa, bác sĩ hoặc gói khám.
     */
    public String embeddingToJson(List<Double> embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (Exception e) {
            log.error("Failed to serialize embedding", e);
            return "[]";
        }
    }

    /**
     * Phương thức: Json sang embedding.
     */
    /**
     * Đọc chuỗi JSON embedding từ database và chuyển lại thành danh sách số.
     * Nếu dữ liệu lỗi hoặc rỗng thì trả về danh sách rỗng để tránh làm hỏng luồng tìm kiếm.
     */
    public List<Double> jsonToEmbedding(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<Double>>() {});
        } catch (Exception e) {
            log.error("Failed to deserialize embedding", e);
            return List.of();
        }
    }

    /**
     * Phương thức: Cosine similarity.
     */
    /**
     * Tính độ tương đồng cosine giữa hai vector embedding.
     * Giá trị càng cao thì câu hỏi và dữ liệu càng gần nghĩa với nhau.
     */
    public double cosineSimilarity(List<Double> a, List<Double> b) {
        if (a.isEmpty() || b.isEmpty() || a.size() != b.size()) return 0.0;

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.size(); i++) {
            dotProduct += a.get(i) * b.get(i);
            normA += a.get(i) * a.get(i);
            normB += b.get(i) * b.get(i);
        }

        double denominator = Math.sqrt(normA) * Math.sqrt(normB);
        return denominator == 0 ? 0.0 : dotProduct / denominator;
    }
}
