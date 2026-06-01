package com.myproject.clinic.serviceregistration.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của ServiceRegistration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRegistrationResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long serviceId;
    private String serviceName;
    private String status;
    private LocalDateTime createdAt;
}
