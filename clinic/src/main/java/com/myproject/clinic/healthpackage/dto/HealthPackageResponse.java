package com.myproject.clinic.healthpackage.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của HealthPackage.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthPackageResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String featureImageUrl;
    private String status;
    private LocalDateTime createdAt;
}
