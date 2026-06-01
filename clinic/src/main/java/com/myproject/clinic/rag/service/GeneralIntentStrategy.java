package com.myproject.clinic.rag.service;

import com.myproject.clinic.rag.dto.ChatRequest;
import com.myproject.clinic.rag.dto.ChatResponse;
import com.myproject.clinic.utils.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Strategy xử lý câu hỏi chung về phòng khám (GENERAL).
 * Gọi LLM để trả lời tự nhiên về: địa chỉ, hotline, giờ mở cửa,
 * hướng dẫn sử dụng chatbot, và các câu chào hỏi thông thường.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GeneralIntentStrategy implements ChatbotIntentStrategy {

    private final LlmService llmService;

    @Override
    public String getSupportedIntent() {
        return "GENERAL";
    }

    /**
     * Hàm xử lý chính cho Intent GENERAL.
     * Nhận câu hỏi, ghép vào System Prompt quy định vai trò của Chatbot,
     * trích xuất tối đa 4 tin nhắn gần nhất từ lịch sử (để hiểu ngữ cảnh nếu có),
     * sau đó gọi LLM để lấy câu trả lời linh hoạt.
     *
     * @param request Yêu cầu chat chứa tin nhắn của người dùng
     * @param state   Map chứa các trạng thái phiên chat (Lịch sử chat)
     * @return Phản hồi DTO trả về cho UI
     */
    @Override
    public ChatResponse handle(ChatRequest request, Map<String, Object> state) {
        log.info("[Strategy] GENERAL");
        List<LlmService.ChatMessage> messages = new ArrayList<>();
        messages.add(new LlmService.ChatMessage("system",
                "Bạn là trợ lý ảo của phòng khám MedPro. Bạn có thể giúp người dùng:\n" +
                "- Phân tích triệu chứng ban đầu\n" +
                "- Tìm kiếm bác sĩ, chuyên khoa\n" +
                "- Thống kê số lượng bác sĩ, chuyên khoa\n\n" +
                "Nếu người dùng hỏi về đặt lịch, hãy hướng dẫn họ dùng tính năng Đặt lịch trực tiếp trên website.\n" +
                "Trả lời ngắn gọn, thân thiện bằng tiếng Việt."));

        // Add history for context (resolve "this doctor", etc.)
        @SuppressWarnings("unchecked")
        List<Map<String, String>> chatHistory =
                (List<Map<String, String>>) state.getOrDefault("chatHistory", new ArrayList<>());

        // Limit to last 4 messages (2 turns) to save tokens but keep immediate context
        int startIdx = Math.max(0, chatHistory.size() - 4);
        for (int i = startIdx; i < chatHistory.size(); i++) {
            Map<String, String> msg = chatHistory.get(i);
            // Bỏ qua tin nhắn user cuối cùng vì sẽ add sau
            if (i == chatHistory.size() - 1 && "user".equals(msg.get("role"))) {
                continue;
            }
            messages.add(new LlmService.ChatMessage(msg.get("role"), msg.get("content")));
        }

        messages.add(new LlmService.ChatMessage("user", request.getMessage()));

        String reply = llmService.chat(messages);
        return ChatResponse.builder()
                .message(reply)
                .intent("GENERAL")
                .step("DONE")
                .build();
    }
}
