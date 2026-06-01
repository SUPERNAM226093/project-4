package com.myproject.clinic.clinicservice.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của ClinicService.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicServiceResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String type;
    private Integer durationMinutes;
    private String createdByName;
    private String featureImageUrl;
}
