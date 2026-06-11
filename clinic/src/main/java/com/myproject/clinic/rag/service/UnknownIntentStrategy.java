package com.myproject.clinic.rag.service;

import com.myproject.clinic.rag.dto.ChatRequest;
import com.myproject.clinic.rag.dto.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class UnknownIntentStrategy implements ChatbotIntentStrategy {
    @Override
    public String getSupportedIntent() {
        return "UNKNOWN";
    }

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
