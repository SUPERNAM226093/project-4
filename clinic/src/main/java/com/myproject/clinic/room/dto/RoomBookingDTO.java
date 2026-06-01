package com.myproject.clinic.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Lớp class RoomBookingDTO trong hệ thống.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomBookingDTO {
    private Long id;
    private String patientName;
    
    private RoomInfo room;
    private UserInfo bookedBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomInfo {
        private Long id;
        private String roomCode;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String fullName;
    }

    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private LocalDateTime actualCheckInAt;
    private LocalDateTime actualCheckOutAt;
    private Integer numberOfPatients;
    private Integer totalNights;
    private BigDecimal estimatedFee;
    private BigDecimal totalPrice;
    private String status;
    private String specialNotes;
    private String contactPhone;
    private String cancelReason;
    private String rejectReason;
    private LocalDateTime createdAt;
}
