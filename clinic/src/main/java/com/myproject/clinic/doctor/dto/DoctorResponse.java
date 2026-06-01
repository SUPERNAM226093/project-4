package com.myproject.clinic.doctor.dto;

import lombok.*;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của Doctor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String specializationName;
    private String licenseNumber;
    private Integer experienceYears;
    private String bio;
    private String featureImageUrl;
    private String status;
}
