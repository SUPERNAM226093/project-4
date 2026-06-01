package com.myproject.clinic.healthpackage.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của HealthPackageBooking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthPackageBookingResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long healthPackageId;
    private String healthPackageName;
    private BigDecimal packagePrice;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private String status;
    private String note;
    private LocalDateTime createdAt;
}
