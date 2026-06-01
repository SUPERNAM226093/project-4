package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể đại diện cho các Dịch vụ khám bệnh/phòng khám cung cấp (ví dụ: Khám tổng quát, Xét nghiệm...).
 */
public class ClinicService {

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

    /** Loại/Phân loại. */
    @Column(length = 50)
    private String type;

    /** Thời lượng thực hiện (tính bằng phút). */
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    /** Người tạo bản ghi (liên kết với thực thể User). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    /** Đường dẫn ảnh đại diện/hình ảnh nổi bật. */
    @Column(name = "feature_image", length = 500)
    private String featureImage;
}
