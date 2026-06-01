package com.myproject.clinic.prescription.dto;

import lombok.*;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho PrescriptionItem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItemRequest {
    private String medicineName;
    private String dosage;
    private String frequency;
    private String duration;
    private String note;
}
