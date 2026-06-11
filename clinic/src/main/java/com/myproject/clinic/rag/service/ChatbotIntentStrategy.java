package com.myproject.clinic.rag.service;

import com.myproject.clinic.rag.dto.ChatRequest;
import com.myproject.clinic.rag.dto.ChatResponse;

import java.util.Map;

/**
 *
 * Interface chung cho tất cả các strategy xử lý intent của Chatbot.
 *
 * Mỗi implementation tương ứng với một loại intent:
 * - StatisticsIntentHandler -> STATISTICS
 * - SymptomIntentHandler -> SYMPTOM
 * - SearchIntentHandler -> SEARCH
 * - GeneralIntentStrategy -> GENERAL
 * - UnknownIntentStrategy -> UNKNOWN
 *
 * IntentStrategyFactory sẽ tự động resolve đúng strategy từ intent string.
 */
public interface ChatbotIntentStrategy {
    String getSupportedIntent();

    /**
     * Xử lý câu hỏi và trả về phản hồi chatbot.
     *
     * @param request ChatRequest chứa sessionId, message, userId
     * @param state   Trạng thái phiên hội thoại hiện tại (từ ChatSessionStore)
     * @return ChatResponse với message, intent, step và data cards
     */
    ChatResponse handle(ChatRequest request, Map<String, Object> state);
}
