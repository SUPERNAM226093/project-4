package com.myproject.clinic.healthpackage.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho HealthPackage.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthPackageRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String status;
}
