package com.myproject.clinic.schedule.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của DoctorSchedule.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorScheduleResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
    private boolean available;
}
