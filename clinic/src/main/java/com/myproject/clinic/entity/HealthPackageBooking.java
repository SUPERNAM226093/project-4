package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "health_package_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể lưu thông tin đăng ký/đặt lịch hẹn sử dụng Gói khám sức khỏe của bệnh nhân.
 */
public class HealthPackageBooking {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Bệnh nhân đặt lịch khám (liên kết với thực thể User). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    /** Gói khám sức khỏe được chọn (liên kết với thực thể HealthPackage). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_package_id", nullable = false)
    private HealthPackage healthPackage;

    /** Ngày hẹn thực hiện gói khám. */
    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    /** Giờ hẹn thực hiện gói khám. */
    @Column(name = "booking_time", nullable = false)
    private LocalTime bookingTime;

    /** Trạng thái hiện tại của thực thể. */
    @Column(length = 30)
    private String status;

    /** Ghi chú thêm thông tin. */
    @Column(columnDefinition = "TEXT")
    private String note;

    /** Thời điểm tạo bản ghi (tự động lưu bởi Hibernate). */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
