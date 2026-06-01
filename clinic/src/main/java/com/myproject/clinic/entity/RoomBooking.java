package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể quản lý việc Đăng ký đặt phòng lưu trú nội trú của bệnh nhân (thời gian check-in, check-out, chi phí...).
 */
public class RoomBooking {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Người thực hiện đặt phòng (liên kết với thực thể User). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booked_by", nullable = false)
    private User bookedBy;

    /** Họ và tên bệnh nhân ở phòng. */
    @Column(nullable = false)
    private String patientName;

    /** Phòng lưu trú được đặt (liên kết với thực thể Room). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /** Ngày giờ dự kiến nhận phòng (Check-in). */
    @Column(nullable = false)
    private LocalDateTime checkInDate;

    /** Ngày giờ dự kiến trả phòng (Check-out). */
    @Column(nullable = false)
    private LocalDateTime checkOutDate;

    /** Thời điểm nhận phòng thực tế. */
    private LocalDateTime actualCheckInAt;
    /** Thời điểm trả phòng thực tế. */
    private LocalDateTime actualCheckOutAt;

    /** Số lượng bệnh nhân lưu trú trong phòng. */
    @Column(nullable = false)
    private Integer numberOfPatients;

    /** Tổng số đêm lưu trú. */
    private Integer totalNights;

    /** Chi phí ước tính ban đầu. */
    @Column(precision = 19, scale = 2)
    private BigDecimal estimatedFee;

    /** Tổng chi phí thực tế phải thanh toán. */
    @Column(precision = 19, scale = 2)
    private BigDecimal totalPrice;

    /** Yêu cầu đặc biệt của khách hàng. */
    @Column(columnDefinition = "TEXT")
    private String specialNotes;

    /** Ghi chú nội bộ của nhân viên/quản trị viên. */
    @Column(columnDefinition = "TEXT")
    private String adminNote;

    /** Lý do từ chối yêu cầu đặt phòng. */
    private String rejectReason;
    /** Lý do hủy yêu cầu đặt phòng. */
    private String cancelReason;

    /** Trạng thái hiện tại của thực thể. */
    @Column(length = 30, nullable = false)
    private String status; // PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, COMPLETED, CANCELLED

    /** Số điện thoại liên hệ. */
    @Column(length = 20)
    private String contactPhone;

    /** Thời điểm tạo bản ghi (tự động lưu bởi Hibernate). */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Thời điểm nhân viên xác nhận đơn đặt phòng. */
    private LocalDateTime confirmedAt;
    /** Thời điểm đơn đặt phòng bị hủy. */
    private LocalDateTime cancelledAt;
}
