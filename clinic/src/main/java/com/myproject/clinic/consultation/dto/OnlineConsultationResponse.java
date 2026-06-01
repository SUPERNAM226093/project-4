package com.myproject.clinic.consultation.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của OnlineConsultation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnlineConsultationResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String specializationName;
    private String serviceName;
    private String phoneNumber;
    private BigDecimal amount;
    private String paymentStatus;
    private String meetingLink;
    private String consultationDate;
    private String consultationTime;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
}
