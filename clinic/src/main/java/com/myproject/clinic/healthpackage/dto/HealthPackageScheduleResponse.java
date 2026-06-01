package com.myproject.clinic.healthpackage.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của HealthPackageSchedule.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthPackageScheduleResponse {
    private Long id;
    private Long healthPackageId;
    private String healthPackageName;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
}
