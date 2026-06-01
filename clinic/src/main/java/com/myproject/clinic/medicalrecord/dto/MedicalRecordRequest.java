package com.myproject.clinic.medicalrecord.dto;

import lombok.*;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho MedicalRecord.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordRequest {
    private Long appointmentId;
    private Long doctorId;
    private String diagnosis;
    private String conclusion;
}
