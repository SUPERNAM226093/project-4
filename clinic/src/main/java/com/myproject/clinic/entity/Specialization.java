package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "specializations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể lưu thông tin về Chuyên khoa y tế (ví dụ: Răng Hàm Mặt, Khoa Nhi, Khoa Nội...).
 */
public class Specialization {

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

    /** Đường dẫn ảnh đại diện/hình ảnh nổi bật. */
    @Column(name = "feature_image", length = 500)
    private String featureImage;

    /** Véc-tơ đặc trưng (dưới dạng JSON) phục vụ tìm kiếm thông minh. */
    @Column(columnDefinition = "JSON")
    private String embedding;

    /** Trạng thái hiện tại của thực thể. */
    @Column(length = 20, nullable = false)
    @Builder.Default
    private String status = "ACTIVE";
}
