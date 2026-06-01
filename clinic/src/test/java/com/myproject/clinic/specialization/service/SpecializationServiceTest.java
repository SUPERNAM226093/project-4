package com.myproject.clinic.specialization.service;

import com.myproject.clinic.config.FileStorageService;
import com.myproject.clinic.entity.Specialization;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.AppointmentRepository;
import com.myproject.clinic.repository.DoctorRepository;
import com.myproject.clinic.repository.OnlineConsultationRepository;
import com.myproject.clinic.repository.SpecializationRepository;
import com.myproject.clinic.specialization.dto.SpecializationRequest;
import com.myproject.clinic.specialization.dto.SpecializationResponse;
import com.myproject.clinic.utils.EmbeddingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecializationServiceTest {

    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private OnlineConsultationRepository onlineConsultationRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private EmbeddingService embeddingService;
    
    @InjectMocks
    private SpecializationService specializationService;

    @Test
    void findAll_returnsList() {
        when(specializationRepository.findByStatus("ACTIVE")).thenReturn(List.of(
                Specialization.builder().id(1L).name("Cardiology").status("ACTIVE").build()
        ));
        List<SpecializationResponse> result = specializationService.findAll();
        assertEquals(1, result.size());
        assertEquals("Cardiology", result.get(0).getName());
    }

    @Test
    void findById_found() {
        when(specializationRepository.findById(1L)).thenReturn(
                Optional.of(Specialization.builder().id(1L).name("Cardiology").description("Heart").build()));
        SpecializationResponse result = specializationService.findById(1L);
        assertEquals("Cardiology", result.getName());
    }

    @Test
    void findById_notFound() {
        when(specializationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> specializationService.findById(99L));
    }

    @Test
    void create_success() {
        when(specializationRepository.save(any())).thenReturn(
                Specialization.builder().id(1L).name("Neurology").description("Brain").build());
        SpecializationResponse result = specializationService.create(
                new SpecializationRequest("Neurology", "Brain", null));
        assertEquals("Neurology", result.getName());
    }

    @Test
    void update_success() {
        when(specializationRepository.findById(1L)).thenReturn(
                Optional.of(Specialization.builder().id(1L).name("Old").build()));
        when(specializationRepository.save(any())).thenReturn(
                Specialization.builder().id(1L).name("New").build());
        SpecializationResponse result = specializationService.update(1L,
                new SpecializationRequest("New", null, null));
        assertEquals("New", result.getName());
    }

    @Test
    void delete_success() {
        Specialization spec = Specialization.builder().id(1L).name("Test").build();
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(spec));
        when(doctorRepository.findBySpecializationId(1L)).thenReturn(new ArrayList<>());
        when(onlineConsultationRepository.findBySpecializationId(1L)).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> specializationService.delete(1L));
        verify(specializationRepository).deleteById(1L);
    }
}
