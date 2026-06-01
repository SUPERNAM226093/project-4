package com.myproject.clinic.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.clinic.entity.ChatSession;
import com.myproject.clinic.exception.ChatSessionPersistenceException;
import com.myproject.clinic.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatSessionStore {

    private static final Pattern SESSION_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{8,64}$");

    private final ChatSessionRepository chatSessionRepository;
    private final ObjectMapper objectMapper;

    @Value("${chat.session.ttl-minutes:30}")
    private long ttlMinutes;

    @Transactional
    public void saveState(String sessionId, Long userId, Map<String, Object> state) {
        validateSessionId(sessionId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plus(Duration.ofMinutes(ttlMinutes));

        String stateJson;
        try {
            stateJson = objectMapper.writeValueAsString(state);
        } catch (JsonProcessingException e) {
            throw new ChatSessionPersistenceException("Không thể serialize trạng thái chat: " + sessionId, e);
        }

        ChatSession entity = chatSessionRepository.findById(sessionId)
                .orElse(ChatSession.builder().sessionId(sessionId).build());
        entity.setUserId(userId);
        entity.setStateJson(stateJson);
        entity.setExpiresAt(expiresAt);
        entity.setUpdatedAt(now);

        try {
            chatSessionRepository.save(entity);
        } catch (Exception e) {
            throw new ChatSessionPersistenceException("Không thể lưu phiên chat: " + sessionId, e);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getState(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return null;
        }
        return chatSessionRepository.findBySessionIdAndExpiresAtAfter(sessionId, LocalDateTime.now())
                .map(this::deserializeState)
                .orElse(null);
    }

    @Transactional
    public void deleteState(String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            chatSessionRepository.deleteById(sessionId);
        }
    }

    private Map<String, Object> deserializeState(ChatSession session) {
        try {
            return objectMapper.readValue(session.getStateJson(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize chat state for session: {}", session.getSessionId(), e);
            return null;
        }
    }

    private void validateSessionId(String sessionId) {
        if (sessionId == null || !SESSION_ID_PATTERN.matcher(sessionId).matches()) {
            throw new ChatSessionPersistenceException("sessionId không hợp lệ");
        }
    }
}
