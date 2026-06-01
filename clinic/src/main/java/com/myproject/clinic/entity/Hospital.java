package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hospitals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể lưu thông tin về Bệnh viện hoặc cơ sở y tế liên kết trong hệ thống.
 */
public class Hospital {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tên gọi hiển thị. */
    @Column(nullable = false, length = 255)
    private String name;

    /** Chuỗi định danh thân thiện với URL. */
    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    /** Trường dữ liệu shortDescription. */
    @Column(name = "short_description", length = 500)
    private String shortDescription;

    /** Mô tả chi tiết. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Địa chỉ thường trú. */
    @Column(length = 500)
    private String address;

    /** Số điện thoại hotline hỗ trợ. */
    @Column(length = 50)
    private String hotline;

    /** Trường dữ liệu workingHours. */
    @Column(name = "working_hours", length = 255)
    private String workingHours;

    /** Comma-separated list of specialty names */
    @Column(columnDefinition = "TEXT")
    private String specialties;

    /** Trường dữ liệu imageUrl. */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** Trường dữ liệu bannerUrl. */
    @Column(name = "banner_url", length = 500)
    private String bannerUrl;

    /** Trường dữ liệu website. */
    @Column(length = 255)
    private String website;

    /** Trường dữ liệu verified. */
    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = false;

    /** Trường dữ liệu active. */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
