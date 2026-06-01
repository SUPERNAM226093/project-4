package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "online_consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể quản lý các cuộc Tư vấn trực tuyến (Video Call) giữa bệnh nhân và bác sĩ, bao gồm trạng thái thanh toán.
 */
public class OnlineConsultation {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Bệnh nhân đặt lịch khám (liên kết với thực thể User). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    /** Bác sĩ tham gia tư vấn trực tuyến. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    /** Chuyên khoa của bác sĩ (liên kết với thực thể Specialization). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    /** Dịch vụ phòng khám được chọn (liên kết với thực thể ClinicService). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ClinicService service;

    /** Trường dữ liệu phoneNumber. */
    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    /** Trường dữ liệu amount. */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /** Trạng thái thanh toán phí tư vấn (PENDING, PAID, COMPLETED...). */
    @Column(name = "payment_status", length = 20)
    @Builder.Default
    private String paymentStatus = "PENDING";

    /** Trường dữ liệu meetingLink. */
    @Column(name = "meeting_link", length = 500)
    private String meetingLink;

    /** Trường dữ liệu consultationDate. */
    @Column(name = "consultation_date")
    private LocalDate consultationDate;

    /** Trường dữ liệu consultationTime. */
    @Column(name = "consultation_time", length = 20)
    private String consultationTime;

    /** Trường dữ liệu expiredAt. */
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    /** Thời điểm tạo bản ghi (tự động lưu bởi Hibernate). */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
