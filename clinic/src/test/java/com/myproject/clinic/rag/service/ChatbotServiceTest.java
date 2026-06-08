package com.myproject.clinic.rag.service;

import com.myproject.clinic.rag.dto.ChatRequest;
import com.myproject.clinic.rag.dto.ChatResponse;
import com.myproject.clinic.repository.DoctorRepository;
import com.myproject.clinic.repository.HealthPackageRepository;
import com.myproject.clinic.repository.SpecializationRepository;
import com.myproject.clinic.utils.ChatSessionStore;
import com.myproject.clinic.utils.EmbeddingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ChatbotServiceTest — kiểm tra luồng điều phối của ChatbotService
 * sau khi refactor theo Strategy + Factory Pattern.
 *
 * Tất cả routing intent được kiểm tra qua IntentStrategyFactory mock,
 * không còn gọi trực tiếp từng handler riêng lẻ.
 */
@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock
    private IntentClassifier intentClassifier;
    @Mock
    private IntentStrategyFactory intentStrategyFactory;
    @Mock
    private DataExtractionService dataExtractionService;
    @Mock
    private ChatSessionStore chatSessionStore;
    @Mock
    private EmbeddingService embeddingService;
    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private HealthPackageRepository healthPackageRepository;

    @InjectMocks
    private ChatbotService chatbotService;

    // ---------------------------------------------------------------
    // Test 1: SEARCH intent — Factory chọn đúng strategy
    // ---------------------------------------------------------------
    @Test
    void processMessage_searchIntent_factoryResolvesStrategy() {
        ChatRequest request = new ChatRequest("session-1", "Tìm bác sĩ mắt", null);
        ChatbotIntentStrategy mockStrategy = mock(ChatbotIntentStrategy.class);

        when(chatSessionStore.getState("session-1")).thenReturn(null);
        when(intentClassifier.classify("Tìm bác sĩ mắt")).thenReturn(List.of("SEARCH"));
        when(intentStrategyFactory.getStrategy("SEARCH")).thenReturn(mockStrategy);
        when(mockStrategy.handle(any(ChatRequest.class), any(Map.class)))
                .thenReturn(ChatResponse.builder().message("Results").intent("SEARCH").step("RESULT").build());

        ChatResponse response = chatbotService.processMessage(request);

        assertNotNull(response);
        assertEquals("SEARCH", response.getIntent());
        verify(intentStrategyFactory).getStrategy("SEARCH");
        verify(mockStrategy).handle(eq(request), any(Map.class));
        verify(chatSessionStore).saveState(eq("session-1"), isNull(), any());
    }

    // ---------------------------------------------------------------
    // Test 2: SYMPTOM intent — delegate sang SymptomIntentHandler
    // ---------------------------------------------------------------
    @Test
    void processMessage_symptomIntent_factoryResolvesStrategy() {
        ChatRequest request = new ChatRequest("session-2", "Tôi đau đầu", null);
        ChatbotIntentStrategy mockStrategy = mock(ChatbotIntentStrategy.class);

        when(chatSessionStore.getState("session-2")).thenReturn(null);
        when(intentClassifier.classify("Tôi đau đầu")).thenReturn(List.of("SYMPTOM"));
        when(intentStrategyFactory.getStrategy("SYMPTOM")).thenReturn(mockStrategy);
        when(mockStrategy.handle(any(ChatRequest.class), any(Map.class)))
                .thenReturn(ChatResponse.builder().message("Analysis").intent("SYMPTOM").step("RESULT").build());

        ChatResponse response = chatbotService.processMessage(request);

        assertNotNull(response);
        assertEquals("SYMPTOM", response.getIntent());
        verify(intentStrategyFactory).getStrategy("SYMPTOM");
    }

    // ---------------------------------------------------------------
    // Test 3: GENERAL intent — gọi LLM trả lời chung
    // ---------------------------------------------------------------
    @Test
    void processMessage_generalIntent_factoryResolvesStrategy() {
        ChatRequest request = new ChatRequest("session-3", "Xin chào", null);
        ChatbotIntentStrategy mockStrategy = mock(ChatbotIntentStrategy.class);

        when(chatSessionStore.getState("session-3")).thenReturn(null);
        when(intentClassifier.classify("Xin chào")).thenReturn(List.of("GENERAL"));
        when(intentStrategyFactory.getStrategy("GENERAL")).thenReturn(mockStrategy);
        when(mockStrategy.handle(any(ChatRequest.class), any(Map.class)))
                .thenReturn(ChatResponse.builder().message("Xin chào!").intent("GENERAL").step("DONE").build());

        ChatResponse response = chatbotService.processMessage(request);

        assertNotNull(response);
        assertEquals("GENERAL", response.getIntent());
    }

    // ---------------------------------------------------------------
    // Test 4: STATISTICS intent — extract params trước khi gọi strategy
    // ---------------------------------------------------------------
    @Test
    void processMessage_statisticsIntent_extractsParamsBeforeStrategy() {
        ChatRequest request = new ChatRequest("session-4", "Bao nhiêu bác sĩ?", null);
        ChatbotIntentStrategy mockStrategy = mock(ChatbotIntentStrategy.class);

        when(chatSessionStore.getState("session-4")).thenReturn(null);
        when(intentClassifier.classify("Bao nhiêu bác sĩ?")).thenReturn(List.of("STATISTICS"));
        when(intentStrategyFactory.getStrategy("STATISTICS")).thenReturn(mockStrategy);
        when(mockStrategy.handle(any(ChatRequest.class), any(Map.class)))
                .thenReturn(ChatResponse.builder().message("5 bác sĩ").intent("STATISTICS").step("RESULT").build());

        ChatResponse response = chatbotService.processMessage(request);

        assertNotNull(response);
        assertEquals("STATISTICS", response.getIntent());
        // dataExtractionService.extract() được gọi vì "STATISTICS" intent
        verify(dataExtractionService).extract(eq("Bao nhiêu bác sĩ?"), any(), any());
    }

    // ---------------------------------------------------------------
    // Test 5: Luôn lưu state sau mỗi tin nhắn
    // ---------------------------------------------------------------
    @Test
    void processMessage_alwaysSavesState() {
        ChatRequest request = new ChatRequest("session-5", "Test", null);
        ChatbotIntentStrategy mockStrategy = mock(ChatbotIntentStrategy.class);

        when(chatSessionStore.getState("session-5")).thenReturn(null);
        when(intentClassifier.classify("Test")).thenReturn(List.of("GENERAL"));
        when(intentStrategyFactory.getStrategy("GENERAL")).thenReturn(mockStrategy);
        when(mockStrategy.handle(any(ChatRequest.class), any(Map.class)))
                .thenReturn(ChatResponse.builder().message("OK").intent("GENERAL").step("DONE").build());

        chatbotService.processMessage(request);

        verify(chatSessionStore).saveState(eq("session-5"), isNull(), any());
    }
}
