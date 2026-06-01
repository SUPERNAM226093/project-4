package com.myproject.clinic.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
public class LlmService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String hfToken;

    private static final String HF_CHAT_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String HF_MODEL = "Qwen/Qwen2.5-72B-Instruct";
    private static final Pattern THINK_PATTERN = Pattern.compile("<think>.*?</think>", Pattern.DOTALL);

    public LlmService(@Value("${hf.token}") String hfToken, 
                      ObjectMapper objectMapper) {
        this.hfToken = hfToken;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                // Timeout 25s để tránh treo vô hạn khi deploy production
                .defaultHeader("Connection", "keep-alive")
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String role;
        private String content;
    }

    // Sentinel dùng để phát hiện khi LLM thất bại (không phải câu trả lời hợp lệ)
    public static final String LLM_ERROR_SENTINEL = "__LLM_ERROR__";

    public String chat(List<ChatMessage> messages) {
        // --- CHỈ SỬ DỤNG HUGGING FACE ---
        if (hfToken != null && !hfToken.isBlank()) {
            try {
                log.info("[LLM] Đang gọi Hugging Face Router...");
                String response = callHuggingFace(messages);
                if (response != null && !response.isBlank()) return response;
            } catch (Exception e) {
                log.error("[LLM] HuggingFace thất bại: {}", e.getMessage());
            }
        }

        // --- KẾT QUẢ CUỐI CÙNG: Trả sentinel để caller biết LLM lỗi ---
        if (hfToken == null || hfToken.isBlank()) {
            log.error("[LLM] Chưa cấu hình API Key cho Hugging Face.");
        } else {
            log.error("[LLM] LLM (Hugging Face) đã thất bại.");
        }
        return LLM_ERROR_SENTINEL;
    }

    private String callHuggingFace(List<ChatMessage> messages) throws Exception {
        // max_tokens=512 đủ cho chatbot y tế, nhanh hơn đáng kể so với 2048
        Map<String, Object> requestBody = Map.of(
                "messages", messages,
                "model", HF_MODEL,
                "stream", false,
                "max_tokens", 512
        );

        String responseBody = webClient.post()
                .uri(HF_CHAT_URL)
                .header("Authorization", "Bearer " + hfToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(25))
                .block();

        JsonNode root = objectMapper.readTree(responseBody);
        if (root.has("error")) {
            log.error("HF Error Body: {}", root.get("error"));
            return null;
        }
        String content = root.path("choices").get(0).path("message").path("content").asText();
        return stripThinkTags(content);
    }



    /**
     * Phân loại intent nhanh — dùng max_tokens=10 vì chỉ cần 1 từ.
     */
    public String classifyIntent(String userMessage) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system",
                "Phân loại intent: SYMPTOM (triệu chứng/bệnh), SEARCH (tìm bác sĩ/khoa/gói khám), GENERAL (khác).\n" +
                "Chỉ trả lời 1 từ."));
        messages.add(new ChatMessage("user", userMessage));

        String result = chat(messages).trim().toUpperCase();

        if (result.contains("SYMPTOM")) return "SYMPTOM";
        if (result.contains("SEARCH")) return "SEARCH";
        return "GENERAL";
    }

    public String generateHeartDiseaseExplanation(int level, Map<String, Double> features) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Bạn là một chuyên gia tư vấn sức khỏe AI. Hệ thống Machine Learning vừa phân tích chỉ số người dùng và đưa ra ")
              .append("Chẩn đoán Mức độ Bệnh Tim là: Cấp độ ").append(level)
              .append(" (Thang: 0 là bình thường, 1-4 là mức độ nghiêm trọng tăng dần).\n\n")
              .append("Dưới đây là các chỉ số y tế lâm sàng của người dùng:\n");
        
        for (Map.Entry<String, Double> entry : features.entrySet()) {
            prompt.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        prompt.append("\nHãy đóng vai thành bác sĩ, giải thích ngắn gọn, xúc tích, thân thiện và chuyên nghiệp về kết quả phân tích này. ")
              .append("Chú ý giải nghĩa một vài chỉ số nếu chúng là nguyên nhân chính gầy rủi ro (như tuổi tác, cholesterol, nhịp tim cao/huyết áp...). ")
              .append("Cuối cùng khuyên người dùng nên đi khám thực tế nếu mức độ >= 1.");

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", prompt.toString()));
        return chat(messages);
    }

    private String stripThinkTags(String text) {
        if (text == null) return "";
        return THINK_PATTERN.matcher(text).replaceAll("").trim();
    }
}
