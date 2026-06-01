package com.myproject.clinic.clinicservice.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho ClinicService.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicServiceRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String type;
    private Integer durationMinutes;
    private Long createdById;
}
