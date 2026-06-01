package com.myproject.clinic.specialization.dto;

import lombok.*;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho Specialization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecializationRequest {
    private String name;
    private String description;
    private String status;
}
