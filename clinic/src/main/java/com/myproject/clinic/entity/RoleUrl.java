package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể cấu hình quyền truy cập API: Phân quyền đường dẫn URL và phương thức HTTP cụ thể cho từng vai trò.
 */
public class RoleUrl {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Vai trò phân quyền hệ thống (liên kết với thực thể Role). */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /** Mẫu đường dẫn URL của API cần bảo mật (ví dụ: /api/doctors/**). */
    @Column(name = "url_pattern", nullable = false)
    private String urlPattern;

    /** Phương thức HTTP tương ứng (GET, POST, PUT, DELETE hoặc ALL). */
    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;

    /** Mô tả chi tiết. */
    @Column(length = 255)
    private String description;

    /** Nguồn phân quyền của API này (MANUAL hoặc MATRIX). */
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_source", nullable = false, length = 16)
    @Builder.Default
    private PermissionSource permissionSource = PermissionSource.MANUAL;

    /** Tên Module trong ma trận phân quyền (nếu có). */
    @Column(name = "matrix_module", length = 64)
    private String matrixModule;
}
