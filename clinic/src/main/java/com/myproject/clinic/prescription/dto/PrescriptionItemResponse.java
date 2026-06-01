package com.myproject.clinic.prescription.dto;

import lombok.*;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của PrescriptionItem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItemResponse {
    private Long id;
    private String medicineName;
    private String dosage;
    private String frequency;
    private String duration;
    private String note;
}
