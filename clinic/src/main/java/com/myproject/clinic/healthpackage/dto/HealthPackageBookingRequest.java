package com.myproject.clinic.healthpackage.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho HealthPackageBooking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthPackageBookingRequest {
    private Long patientId;
    private Long healthPackageId;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private String status;
    private String note;
}
