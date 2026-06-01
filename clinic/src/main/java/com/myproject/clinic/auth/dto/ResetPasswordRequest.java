package com.myproject.clinic.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho ResetPassword.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
