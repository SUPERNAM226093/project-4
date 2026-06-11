package com.myproject.clinic.rag.controller;

import com.myproject.clinic.rag.dto.ChatRequest;
import com.myproject.clinic.rag.dto.ChatResponse;
import com.myproject.clinic.rag.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatbotService chatbotService;

    /**
     * 
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = chatbotService.processMessage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint quản trị dùng để tính toán lại vector (embedding) cho toàn bộ dữ
     * liệu
     * chuyên khoa, bác sĩ
     */
    @PostMapping("/reindex")
    public ResponseEntity<Map<String, String>> reindex() {
        chatbotService.reindexAll();
        return ResponseEntity.ok(Map.of("status", "Reindex completed successfully"));
    }
}
