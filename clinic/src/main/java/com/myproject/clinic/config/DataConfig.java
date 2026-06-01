package com.myproject.clinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Cấu hình truy xuất dữ liệu (DataConfig).
 * Dùng để kích hoạt tính năng JPA Repositories và chỉ định gói (package) chứa các Repository giao tiếp với Database.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
    "com.myproject.clinic.repository"
})
public class DataConfig {
}
