package com.myproject.clinic.rag.service;

import com.myproject.clinic.entity.*;
import com.myproject.clinic.rag.dto.ChatResponse;
import com.myproject.clinic.repository.DoctorRepository;
import com.myproject.clinic.repository.HealthPackageRepository;
import com.myproject.clinic.repository.SpecializationRepository;
import com.myproject.clinic.utils.EmbeddingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchIntentHandlerTest {

    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private HealthPackageRepository healthPackageRepository;
    @Mock
    private EmbeddingService embeddingService;

    @InjectMocks
    private SearchIntentHandler searchIntentHandler;

    @Test
    void handle_noResults_returnsNotFoundMessage() {
        Map<String, Object> state = new HashMap<>();

        when(embeddingService.getEmbedding(anyString())).thenReturn(List.of(0.1, 0.2));
        when(specializationRepository.findAll()).thenReturn(List.of());
        when(doctorRepository.findAll()).thenReturn(List.of());
        when(healthPackageRepository.findAll()).thenReturn(List.of());

        ChatResponse response = searchIntentHandler.handle("something random", state);

        assertEquals("SEARCH", response.getIntent());
        assertTrue(response.getMessage().contains("không tìm thấy"));
        assertNull(response.getSpecializations());
        assertNull(response.getDoctors());
        assertNull(response.getHealthPackages());
    }

    @Test
    void handle_withResults_returnsCards() {
        Map<String, Object> state = new HashMap<>();

        Specialization spec = Specialization.builder()
                .id(1L).name("Tim mạch").description("Heart").embedding("[0.1,0.2]").build();
        User user = User.builder().id(1L).fullName("Dr. Test").build();
        Specialization doctorSpec = Specialization.builder().id(1L).name("Tim mạch").build();
        Doctor doctor = Doctor.builder()
                .id(1L).user(user).specialization(doctorSpec).bio("Expert").embedding("[0.1,0.2]").build();
        HealthPackage hp = HealthPackage.builder()
                .id(1L).name("Gói khám tổng quát").description("Full checkup").price(BigDecimal.valueOf(500000))
                .embedding("[0.1,0.2]").build();

        when(embeddingService.getEmbedding(anyString())).thenReturn(List.of(0.1, 0.2));
        when(specializationRepository.findAll()).thenReturn(List.of(spec));
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        when(healthPackageRepository.findAll()).thenReturn(List.of(hp));
        when(embeddingService.jsonToEmbedding("[0.1,0.2]")).thenReturn(List.of(0.1, 0.2));
        when(embeddingService.cosineSimilarity(any(), any())).thenReturn(0.8);

        ChatResponse response = searchIntentHandler.handle("khám tim", state);

        assertEquals("SEARCH", response.getIntent());
        assertEquals("RESULT", response.getStep());
        assertNotNull(response.getSpecializations());
        assertNotNull(response.getDoctors());
        assertNotNull(response.getHealthPackages());
        assertEquals(1, response.getSpecializations().size());
        assertEquals(1, response.getDoctors().size());
        assertEquals(1, response.getHealthPackages().size());
    }

    @Test
    void handle_filtersBelowThreshold() {
        Map<String, Object> state = new HashMap<>();

        Specialization spec = Specialization.builder()
                .id(1L).name("X").description("Y").embedding("[0.1]").build();

        when(embeddingService.getEmbedding(anyString())).thenReturn(List.of(0.1));
        when(specializationRepository.findAll()).thenReturn(List.of(spec));
        when(doctorRepository.findAll()).thenReturn(List.of());
        when(healthPackageRepository.findAll()).thenReturn(List.of());
        when(embeddingService.jsonToEmbedding("[0.1]")).thenReturn(List.of(0.1));
        when(embeddingService.cosineSimilarity(any(), any())).thenReturn(0.1); // Below 0.3 threshold

        ChatResponse response = searchIntentHandler.handle("unrelated query", state);

        assertTrue(response.getMessage().contains("không tìm thấy"));
    }
}
