package com.myproject.clinic.rag.service;

import com.myproject.clinic.rag.dto.ChatRequest;
import com.myproject.clinic.rag.dto.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Strategy xử lý câu hỏi ngoài phạm vi phòng khám (UNKNOWN).
 * Trả về phản hồi từ chối lịch sự, không gọi LLM để tiết kiệm API.
 */
@Component
@Slf4j
public class UnknownIntentStrategy implements ChatbotIntentStrategy {

    /** Trả về intent UNKNOWN cho các câu hỏi ngoài phạm vi y tế/phòng khám. */
    @Override
    public String getSupportedIntent() {
        return "UNKNOWN";
    }

    /**
     * Hàm xử lý khi không nhận diện được mục đích (Intent) hoặc người dùng
     * hỏi những câu hoàn toàn nằm ngoài phạm vi y tế (VD: giá vàng, bóng đá, thời tiết...).
     * Sẽ trả về một câu trả lời được code cứng (hard-code) để từ chối khéo, không gọi LLM để tiết kiệm chi phí.
     */
    @Override
    public ChatResponse handle(ChatRequest request, Map<String, Object> state) {
        log.info("[Strategy] UNKNOWN");
        return ChatResponse.builder()
                .message("Xin lỗi, tôi là trợ lý ảo chuyên về y tế và phòng khám. " +
                         "Tôi chỉ có thể hỗ trợ bạn về:\n" +
                         "• Thông tin bác sĩ, chuyên khoa\n" +
                         "• Phân tích triệu chứng ban đầu\n\n" +
                         "Bạn có câu hỏi nào về sức khỏe không ạ? 😊")
                .intent("UNKNOWN")
                .step("DONE")
                .build();
    }
}
