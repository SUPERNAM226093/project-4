package com.myproject.clinic.serviceregistration.dto;

import lombok.*;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho ServiceRegistration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRegistrationRequest {
    private Long userId;
    private Long serviceId;
    private String status;
}
