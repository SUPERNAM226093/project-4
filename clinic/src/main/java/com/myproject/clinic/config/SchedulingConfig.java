package com.myproject.clinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Cấu hình lập lịch công việc tự động (SchedulingConfig).
 * Dùng để kích hoạt tính năng chạy các tác vụ định kỳ của Spring Boot (quét các phương thức được đánh dấu @Scheduled).
 * Ví dụ: Tự động dọn dẹp các lịch hẹn hết hạn, gửi email nhắc nhở lịch khám...
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
