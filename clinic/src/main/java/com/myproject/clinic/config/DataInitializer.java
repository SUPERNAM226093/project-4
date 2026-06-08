package com.myproject.clinic.config;

import com.myproject.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer — Chạy tự động khi ứng dụng khởi động.
 *
 * Nhiệm vụ duy nhất: Đảm bảo tài khoản admin@gmail.com có mật khẩu đúng.
 * Phân quyền đã được cố định (hardcode) trong SecurityConfig theo vai trò.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Đặt lại mật khẩu cho tài khoản admin mặc định
        userRepository.findByEmail("admin@gmail.com").ifPresent(admin -> {
            admin.setPasswordHash(passwordEncoder.encode("123456"));
            userRepository.save(admin);
            System.out.println(">>> [DataInitializer] Đã đặt lại mật khẩu cho admin@gmail.com");
        });
        System.out.println(">>> [DataInitializer] Hoàn tất khởi tạo.");
    }
}
