package com.myproject.clinic.appointment.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của Appointment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String serviceName;
    private String healthPackageName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;
    private String note;
    private LocalDateTime createdAt;
}
