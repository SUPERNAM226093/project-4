package com.myproject.clinic.rag.service;

import com.myproject.clinic.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Lớp chịu trách nhiệm dọn dẹp các phiên trò chuyện (Chat Session) đã hết hạn.
 * Chạy ngầm định kỳ bằng Spring Scheduler để tránh làm đầy database.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatSessionCleanupTask {

    private final ChatSessionRepository chatSessionRepository;

    /**
     * Tác vụ tự động (Cron job / Scheduled task) chạy mỗi 10 phút (600,000 ms).
     * Xóa toàn bộ các bản ghi ChatSession có thời gian hết hạn (expiresAt) trước thời điểm hiện tại.
     * Yêu cầu có Transaction để đảm bảo tính toàn vẹn dữ liệu khi thao tác xóa.
     */
    @Scheduled(fixedRate = 600_000)
    @Transactional
    public void purgeExpiredSessions() {
        int deleted = chatSessionRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Đã xóa {} phiên chat hết hạn", deleted);
        }
    }
}
