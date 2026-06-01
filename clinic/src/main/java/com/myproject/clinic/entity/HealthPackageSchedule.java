package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "health_package_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể quản lý Lịch khám khả dụng của các gói khám sức khỏe.
 */
public class HealthPackageSchedule {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Gói khám sức khỏe được chọn (liên kết với thực thể HealthPackage). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_package_id", nullable = false)
    private HealthPackage healthPackage;

    /** Ngày làm việc. */
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    /** Giờ bắt đầu làm việc. */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /** Giờ kết thúc làm việc. */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /** Thời điểm tạo bản ghi (tự động lưu bởi Hibernate). */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
