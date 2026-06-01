package com.myproject.clinic.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của Chat.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private String message;
    private String intent;
    private String step;
    private List<CardItem> specializations;
    private List<DoctorCard> doctors;
    private List<CardItem> healthPackages;
    private AppointmentConfirmation appointmentConfirmation;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CardItem {
        private Long id;
        private String name;
        private String description;
        private String featureImageUrl;
        private String type; // "specialization", "health_package", "service"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoctorCard {
        private Long id;
        private String fullName;
        private String specializationName;
        private String bio;
        private String featureImageUrl;
        private Integer experienceYears;
        private List<ScheduleSlot> schedules;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScheduleSlot {
        private Long id;
        private String workDate;
        private String startTime;
        private String endTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppointmentConfirmation {
        private Long appointmentId;
        private String doctorName;
        private String appointmentDate;
        private String appointmentTime;
        private String status;
    }
}
