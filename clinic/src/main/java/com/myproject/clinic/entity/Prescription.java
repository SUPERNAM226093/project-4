package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể biểu diễn Đơn thuốc được bác sĩ kê sau cuộc khám bệnh.
 */
public class Prescription {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Trường dữ liệu medicalRecord. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false, unique = true)
    private MedicalRecord medicalRecord;

    /** Bác sĩ đảm nhận lịch khám/tư vấn (liên kết với thực thể Doctor). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    /** Thời điểm tạo bản ghi (tự động lưu bởi Hibernate). */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Trường dữ liệu items. */
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PrescriptionItem> items = new ArrayList<>();
}
