package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể lưu trữ mã Token dùng cho chức năng đặt lại/khôi phục mật khẩu qua Email.
 */
public class PasswordResetToken {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tài khoản người dùng liên kết (liên kết 1-1 với thực thể User). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Trường dữ liệu tokenHash. */
    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    /** Trường dữ liệu expiresAt. */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** Trường dữ liệu usedAt. */
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    /** Thời điểm tạo bản ghi (tự động lưu bởi Hibernate). */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
