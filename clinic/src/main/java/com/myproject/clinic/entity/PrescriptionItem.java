package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prescription_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể lưu trữ Chi tiết từng loại thuốc trong đơn thuốc (tên thuốc, số lượng, liều dùng, cách dùng).
 */
public class PrescriptionItem {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Đơn thuốc chứa danh mục thuốc này (liên kết với thực thể Prescription). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    /** Tên thuốc hoặc biệt dược kê đơn. */
    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    /** Liều lượng sử dụng (ví dụ: 1 viên/lần). */
    @Column(length = 100)
    private String dosage;

    /** Trường dữ liệu frequency. */
    @Column(length = 100)
    private String frequency;

    /** Trường dữ liệu duration. */
    @Column(length = 100)
    private String duration;

    /** Ghi chú thêm thông tin. */
    @Column(columnDefinition = "TEXT")
    private String note;
}
