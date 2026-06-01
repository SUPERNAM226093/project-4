package com.myproject.clinic.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho Chat.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String sessionId;
    private String message;
    private Long userId;
}
