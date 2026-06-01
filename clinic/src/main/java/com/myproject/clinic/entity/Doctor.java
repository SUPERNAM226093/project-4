package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể lưu thông tin hồ sơ chi tiết của Bác sĩ (liên kết 1-1 với tài khoản User).
 */
public class Doctor {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tài khoản người dùng liên kết (liên kết 1-1 với thực thể User). */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** Chuyên khoa của bác sĩ (liên kết với thực thể Specialization). */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    /** Số giấy phép hành nghề y khoa. */
    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    /** Số năm kinh nghiệm hành nghề. */
    @Column(name = "experience_years")
    private Integer experienceYears;

    /** Tiểu sử/Giới thiệu ngắn về bác sĩ. */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /** Đường dẫn ảnh đại diện/hình ảnh nổi bật. */
    @Column(name = "feature_image", length = 500)
    private String featureImage;

    /** Véc-tơ đặc trưng (dưới dạng JSON) phục vụ tìm kiếm thông minh. */
    @Column(columnDefinition = "JSON")
    private String embedding;
}
