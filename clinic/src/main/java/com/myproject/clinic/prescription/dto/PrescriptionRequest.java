package com.myproject.clinic.prescription.dto;

import lombok.*;

import java.util.List;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho Prescription.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionRequest {
    private Long medicalRecordId;
    private Long doctorId;
    private List<PrescriptionItemRequest> items;
}
