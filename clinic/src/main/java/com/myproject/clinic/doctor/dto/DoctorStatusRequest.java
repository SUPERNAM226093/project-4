package com.myproject.clinic.doctor.dto;

import lombok.*;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho DoctorStatus.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStatusRequest {
    private String status;
}
