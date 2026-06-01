package com.myproject.clinic.rag.controller;

import com.myproject.clinic.rag.dto.ChatRequest;
import com.myproject.clinic.rag.dto.ChatResponse;
import com.myproject.clinic.rag.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho Chatbot RAG.
 * Cung cấp các endpoint để Frontend có thể gửi tin nhắn và nhận phản hồi từ Chatbot.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatbotService chatbotService;

    /**
     * Endpoint chính để nhận tin nhắn từ người dùng và trả về phản hồi của Chatbot.
     * Phương thức POST: /api/chat
     * 
     * @param request Dữ liệu đầu vào chứa sessionId, tin nhắn, và userId
     * @return Đối tượng ChatResponse chứa câu trả lời và các danh sách thẻ (bác sĩ, chuyên khoa...)
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = chatbotService.processMessage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint quản trị dùng để tính toán lại vector (embedding) cho toàn bộ dữ liệu 
     * chuyên khoa, bác sĩ, gói khám trong Database. 
     * Phục vụ cho tính năng tìm kiếm ngữ nghĩa (Semantic Search).
     */
    @PostMapping("/reindex")
    public ResponseEntity<Map<String, String>> reindex() {
        chatbotService.reindexAll();
        return ResponseEntity.ok(Map.of("status", "Reindex completed successfully"));
    }
}
