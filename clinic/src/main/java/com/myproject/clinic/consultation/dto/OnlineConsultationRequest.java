package com.myproject.clinic.consultation.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho OnlineConsultation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnlineConsultationRequest {
    private Long patientId;
    private Long doctorId;
    private Long specializationId;
    private Long serviceId;
    private String phoneNumber;
    private BigDecimal amount;
    private String paymentStatus;
    private String meetingLink;
    private String consultationDate; // LocalDate as String for simple parsing
    private String consultationTime;
}
