package com.myproject.clinic.prescription.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của Prescription.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionResponse {
    private Long id;
    private Long medicalRecordId;
    private Long doctorId;
    private String doctorName;
    private LocalDateTime createdAt;
    private List<PrescriptionItemResponse> items;
}
