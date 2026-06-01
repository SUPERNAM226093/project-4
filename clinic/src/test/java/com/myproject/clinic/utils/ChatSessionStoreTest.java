package com.myproject.clinic.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.clinic.entity.ChatSession;
import com.myproject.clinic.exception.ChatSessionPersistenceException;
import com.myproject.clinic.repository.ChatSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatSessionStoreTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    private ChatSessionStore chatSessionStore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        chatSessionStore = new ChatSessionStore(chatSessionRepository, objectMapper);
        ReflectionTestUtils.setField(chatSessionStore, "ttlMinutes", 30L);
    }

    @Test
    void saveState_persistsJsonAndExpiry() {
        when(chatSessionRepository.findById("session-123")).thenReturn(Optional.empty());
        when(chatSessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> state = new HashMap<>();
        state.put("intent", "SEARCH");

        chatSessionStore.saveState("session-123", 1L, state);

        ArgumentCaptor<ChatSession> captor = ArgumentCaptor.forClass(ChatSession.class);
        verify(chatSessionRepository).save(captor.capture());
        ChatSession saved = captor.getValue();
        assertEquals("session-123", saved.getSessionId());
        assertEquals(1L, saved.getUserId());
        assertNotNull(saved.getStateJson());
        assertTrue(saved.getExpiresAt().isAfter(LocalDateTime.now().plusMinutes(29)));
    }

    @Test
    void saveState_invalidSessionId_throws() {
        assertThrows(ChatSessionPersistenceException.class,
                () -> chatSessionStore.saveState("bad id!", null, Map.of()));
    }

    @Test
    void getState_expiredSession_returnsNull() {
        when(chatSessionRepository.findBySessionIdAndExpiresAtAfter(eq("session-123"), any()))
                .thenReturn(Optional.empty());

        assertNull(chatSessionStore.getState("session-123"));
    }

    @Test
    void getState_activeSession_returnsMap() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of("intent", "SEARCH"));
        ChatSession session = ChatSession.builder()
                .sessionId("session-123")
                .stateJson(json)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .updatedAt(LocalDateTime.now())
                .build();
        when(chatSessionRepository.findBySessionIdAndExpiresAtAfter(eq("session-123"), any()))
                .thenReturn(Optional.of(session));

        Map<String, Object> result = chatSessionStore.getState("session-123");
        assertNotNull(result);
        assertEquals("SEARCH", result.get("intent"));
    }
}
