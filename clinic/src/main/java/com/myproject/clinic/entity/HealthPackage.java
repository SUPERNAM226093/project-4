package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể đại diện cho các Gói khám sức khỏe tổng quát hoặc gói khám chuyên sâu.
 */
public class HealthPackage {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tên gọi hiển thị. */
    @Column(nullable = false)
    private String name;

    /** Mô tả chi tiết. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Giá tiền/Chi phí. */
    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    /** Đường dẫn ảnh đại diện/hình ảnh nổi bật. */
    @Column(name = "feature_image", length = 500)
    private String featureImage;

    /** Véc-tơ đặc trưng (dưới dạng JSON) phục vụ tìm kiếm thông minh. */
    @Column(columnDefinition = "JSON")
    private String embedding;

    /** Thời điểm tạo bản ghi (tự động lưu bởi Hibernate). */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Trạng thái hiện tại của thực thể. */
    @Column(length = 20, nullable = false)
    @Builder.Default
    private String status = "ACTIVE";
}
