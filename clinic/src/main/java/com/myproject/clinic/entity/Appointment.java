package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể biểu diễn thông tin Lịch hẹn khám bệnh giữa bệnh nhân và bác sĩ/phòng khám.
 */
public class Appointment {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Bệnh nhân đặt lịch khám (liên kết với thực thể User). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    /** Bác sĩ khám bệnh liên quan. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    /** Dịch vụ phòng khám được đặt. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ClinicService service;

    /** Khung giờ khám của bác sĩ. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private DoctorSchedule schedule;

    /** Gói khám sức khỏe liên kết (nếu đặt theo gói khám). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_package_id")
    private HealthPackage healthPackage;

    /** Ngày hẹn khám bệnh. */
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    /** Giờ hẹn khám bệnh cụ thể. */
    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

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
