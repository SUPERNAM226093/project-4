package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể đại diện cho Tài khoản người dùng trong hệ thống (bao gồm thông tin cá nhân, email đăng nhập, vai trò...).
 */
public class User {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Địa chỉ Email của người dùng (dùng để đăng nhập hệ thống, là duy nhất). */
    @Column(nullable = false, unique = true)
    private String email;

    /** Mật khẩu đã được mã hóa (bằng bcrypt). */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /** Họ và tên đầy đủ. */
    @Column(name = "full_name")
    private String fullName;

    /** Số điện thoại cá nhân. */
    @Column(length = 20)
    private String phone;

    /** Ngày tháng năm sinh. */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /** Giới tính (Nam, Nữ, Khác). */
    @Column(length = 10)
    private String gender;

    /** Địa chỉ thường trú. */
    @Column(columnDefinition = "TEXT")
    private String address;

    /** Tên vai trò (ADMIN, DOCTOR, PATIENT, STAFF). */
    @Column(name = "role_name", length = 50)
    private String roleName;

    /** Trạng thái hiện tại của thực thể. */
    @Column(length = 20)
    private String status;

    /** Thời điểm tạo bản ghi (tự động lưu bởi Hibernate). */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Thời điểm cập nhật bản ghi gần nhất (tự động cập nhật). */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
