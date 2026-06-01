package com.myproject.clinic.healthpackage.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho HealthPackageSchedule.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthPackageScheduleRequest {
    private Long healthPackageId;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
