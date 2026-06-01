package com.myproject.clinic.rag.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * === FACTORY PATTERN ===
 *
 * Factory chọn đúng Strategy handler dựa trên intent string.
 *
 * Spring tự inject toàn bộ bean implement ChatbotIntentStrategy.
 * Factory gom vào Map<intent, strategy> khi khởi động.
 *
 * Lookup O(1) — không có if/else, không có switch/case.
 * Nếu intent không hợp lệ -> fallback UnknownIntentStrategy.
 */
@Component
@Slf4j
public class IntentStrategyFactory {

    private final Map<String, ChatbotIntentStrategy> strategyMap;
    private final ChatbotIntentStrategy unknownStrategy;

    /**
     * Spring inject toàn bộ implementation của ChatbotIntentStrategy.
     * Factory tự build strategyMap từ getSupportedIntent() của mỗi strategy.
     */
    public IntentStrategyFactory(List<ChatbotIntentStrategy> strategies) {
        this.strategyMap = new HashMap<>();

        ChatbotIntentStrategy unknown = null;
        for (ChatbotIntentStrategy strategy : strategies) {
            strategyMap.put(strategy.getSupportedIntent(), strategy);
            if ("UNKNOWN".equals(strategy.getSupportedIntent())) {
                unknown = strategy;
            }
            log.info("[IntentStrategyFactory] Registered strategy: {} -> {}",
                    strategy.getSupportedIntent(), strategy.getClass().getSimpleName());
        }

        this.unknownStrategy = unknown != null ? unknown : strategies.get(0);
    }

    /**
     * Hàm lấy ra Strategy (chiến lược) tương ứng để xử lý một intent cụ thể.
     * Nếu intent bị rỗng (null) hoặc không nằm trong danh sách hỗ trợ, hệ thống
     * sẽ tự động trả về UnknownIntentStrategy để báo lỗi lịch sự với người dùng.
     *
     * @param intent Chuỗi văn bản đại diện cho intent (VD: "STATISTICS", "SYMPTOM", "SEARCH")
     * @return Lớp implement ChatbotIntentStrategy phù hợp nhất để xử lý
     */
    public ChatbotIntentStrategy getStrategy(String intent) {
        if (intent == null) return unknownStrategy;
        ChatbotIntentStrategy strategy = strategyMap.get(intent.toUpperCase());
        if (strategy == null) {
            log.warn("[IntentStrategyFactory] No strategy for intent '{}', using UNKNOWN", intent);
            return unknownStrategy;
        }
        return strategy;
    }
}
