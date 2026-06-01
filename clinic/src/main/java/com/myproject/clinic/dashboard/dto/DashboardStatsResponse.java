package com.myproject.clinic.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của
 * DashboardStats.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private SummaryStats summary;
    private List<DirectAppointmentChartPoint> directChartData;
    private List<TodayAppointmentDTO> todayAppointments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryStats {
        private long totalPackageBookings;
        private long totalRoomBookings;
        private long newPatients;
        private long totalConfirmedOrCompletedAppointments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DirectAppointmentChartPoint {
        private String date;
        private String fullDate;
        private long completedCount;
        private long otherCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodayAppointmentDTO {
        private String id;
        private String patientName;
        private String doctorName;
        private String time;
        private String sourceType;
        private String status;
    }
}
