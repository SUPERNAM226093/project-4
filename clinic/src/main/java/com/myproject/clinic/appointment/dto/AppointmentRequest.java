package com.myproject.clinic.appointment.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho Appointment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {
    private Long patientId;
    private Long doctorId;
    private Long serviceId;
    private Long scheduleId;
    private Long healthPackageId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;
    private String note;
}
