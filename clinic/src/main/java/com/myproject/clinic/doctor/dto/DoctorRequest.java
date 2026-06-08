package com.myproject.clinic.doctor.dto;

import lombok.*;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho Doctor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorRequest {
    private Long userId;
    private Long specializationId;
    private Long clinicId;
    private Integer experienceYears;
    private String bio;
}
