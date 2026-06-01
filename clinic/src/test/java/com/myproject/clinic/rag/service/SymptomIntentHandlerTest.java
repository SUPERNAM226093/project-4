package com.myproject.clinic.rag.service;

import com.myproject.clinic.entity.Specialization;
import com.myproject.clinic.rag.dto.ChatResponse;
import com.myproject.clinic.repository.SpecializationRepository;
import com.myproject.clinic.utils.EmbeddingService;
import com.myproject.clinic.utils.LlmService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SymptomIntentHandlerTest {

    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private EmbeddingService embeddingService;
    @Mock
    private LlmService llmService;

    @InjectMocks
    private SymptomIntentHandler symptomIntentHandler;

    @Test
    void handle_noSpecs_returnsNotFound() {
        Map<String, Object> state = new HashMap<>();

        when(embeddingService.getEmbedding(anyString())).thenReturn(List.of(0.1, 0.2));
        when(specializationRepository.findAll()).thenReturn(List.of());

        ChatResponse response = symptomIntentHandler.handle("đau đầu chóng mặt", state);

        assertEquals("SYMPTOM", response.getIntent());
        assertTrue(response.getMessage().contains("không tìm thấy"));
    }

    @Test
    void handle_withMatchingSpecs_returnsCardsAndAnalysis() {
        Map<String, Object> state = new HashMap<>();

        Specialization spec1 = Specialization.builder()
                .id(1L).name("Thần kinh").description("Neurology")
                .featureImage("specs/neuro.jpg").embedding("[0.1,0.2]").build();
        Specialization spec2 = Specialization.builder()
                .id(2L).name("Tim mạch").description("Cardiology")
                .embedding("[0.3,0.4]").build();

        when(embeddingService.getEmbedding(anyString())).thenReturn(List.of(0.1, 0.2));
        when(specializationRepository.findAll()).thenReturn(List.of(spec1, spec2));
        when(embeddingService.jsonToEmbedding("[0.1,0.2]")).thenReturn(List.of(0.1, 0.2));
        when(embeddingService.jsonToEmbedding("[0.3,0.4]")).thenReturn(List.of(0.3, 0.4));
        when(embeddingService.cosineSimilarity(any(), eq(List.of(0.1, 0.2)))).thenReturn(0.9);
        when(embeddingService.cosineSimilarity(any(), eq(List.of(0.3, 0.4)))).thenReturn(0.7);
        when(llmService.chat(any())).thenReturn("Bạn có thể bị đau đầu do thần kinh");

        ChatResponse response = symptomIntentHandler.handle("đau đầu chóng mặt", state);

        assertEquals("SYMPTOM", response.getIntent());
        assertEquals("RESULT", response.getStep());
        assertNotNull(response.getSpecializations());
        assertFalse(response.getSpecializations().isEmpty());
        assertEquals("Thần kinh", response.getSpecializations().get(0).getName());
        assertEquals("specialization", response.getSpecializations().get(0).getType());
    }

    @Test
    void handle_callsLlmForAnalysis() {
        Map<String, Object> state = new HashMap<>();

        Specialization spec = Specialization.builder()
                .id(1L).name("Nội tổng quát").description("General")
                .embedding("[0.5,0.5]").build();

        when(embeddingService.getEmbedding(anyString())).thenReturn(List.of(0.5, 0.5));
        when(specializationRepository.findAll()).thenReturn(List.of(spec));
        when(embeddingService.jsonToEmbedding("[0.5,0.5]")).thenReturn(List.of(0.5, 0.5));
        when(embeddingService.cosineSimilarity(any(), any())).thenReturn(0.8);
        when(llmService.chat(any())).thenReturn("Phân tích triệu chứng");

        ChatResponse response = symptomIntentHandler.handle("mệt mỏi", state);

        verify(llmService).chat(any());
        assertEquals("Phân tích triệu chứng", response.getMessage());
    }
}
