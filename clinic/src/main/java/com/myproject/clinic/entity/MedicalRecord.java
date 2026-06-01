package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể lưu trữ Hồ sơ bệnh án của bệnh nhân sau mỗi lần khám bệnh (triệu chứng, chẩn đoán, bác sĩ khám...).
 */
public class MedicalRecord {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Lịch hẹn khám dẫn đến hồ sơ bệnh án này. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    /** Bác sĩ đảm nhận lịch khám/tư vấn (liên kết với thực thể Doctor). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    /** Trường dữ liệu diagnosis. */
    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    /** Trường dữ liệu conclusion. */
    @Column(columnDefinition = "TEXT")
    private String conclusion;

    /** Thời điểm tạo bản ghi (tự động lưu bởi Hibernate). */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
