package com.myproject.clinic.medicalrecord.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của MedicalRecord.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordResponse {
    private Long id;
    private Long appointmentId;
    private Long doctorId;
    private String doctorName;
    private String patientName;
    private String diagnosis;
    private String conclusion;
    private LocalDateTime createdAt;
}
