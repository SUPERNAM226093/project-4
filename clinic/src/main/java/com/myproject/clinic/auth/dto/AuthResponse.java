package com.myproject.clinic.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của Auth.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long userId;
    private String token;
    private String email;
    private String fullName;
    private String role;
}
