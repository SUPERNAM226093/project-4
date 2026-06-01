package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể lưu thông tin Vai trò người dùng (ví dụ: ADMIN, PATIENT, DOCTOR, STAFF).
 */
public class Role {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tên gọi hiển thị. */
    @Column(nullable = false, length = 50)
    private String name;

    /** Trạng thái kích hoạt của Role */
    @Column(columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean isActive = true;
}
