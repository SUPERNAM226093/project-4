package com.myproject.clinic.room.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho RoomBooking.
 */
@Data
public class RoomBookingRequest {
    private Long roomId;
    private String patientName;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Integer numberOfPatients;
    private String specialNotes;
    private String contactPhone;
    private String status;
    private Long bookedById;
}
