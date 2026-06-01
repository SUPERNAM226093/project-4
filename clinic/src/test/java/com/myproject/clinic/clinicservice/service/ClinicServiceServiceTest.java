package com.myproject.clinic.clinicservice.service;

import com.myproject.clinic.clinicservice.dto.ClinicServiceRequest;
import com.myproject.clinic.clinicservice.dto.ClinicServiceResponse;
import com.myproject.clinic.config.FileStorageService;
import com.myproject.clinic.entity.ClinicService;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.ClinicServiceRepository;
import com.myproject.clinic.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicServiceServiceTest {

    @Mock
    private ClinicServiceRepository clinicServiceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FileStorageService fileStorageService;
    @InjectMocks
    private ClinicServiceService clinicServiceService;

    private ClinicService createService() {
        return ClinicService.builder().id(1L).name("Blood Test").description("Basic blood test")
                .price(new BigDecimal("50.00")).type("LAB").durationMinutes(30).build();
    }

    @Test
    void findAll_returnsList() {
        when(clinicServiceRepository.findAll()).thenReturn(List.of(createService()));
        List<ClinicServiceResponse> result = clinicServiceService.findAll();
        assertEquals(1, result.size());
        assertEquals("Blood Test", result.get(0).getName());
    }

    @Test
    void findById_found() {
        when(clinicServiceRepository.findById(1L)).thenReturn(Optional.of(createService()));
        ClinicServiceResponse result = clinicServiceService.findById(1L);
        assertEquals("Blood Test", result.getName());
    }

    @Test
    void findById_notFound() {
        when(clinicServiceRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> clinicServiceService.findById(99L));
    }

    @Test
    void create_success() {
        when(clinicServiceRepository.save(any())).thenReturn(createService());
        ClinicServiceRequest request = ClinicServiceRequest.builder()
                .name("Blood Test").price(new BigDecimal("50.00")).build();
        ClinicServiceResponse result = clinicServiceService.create(request);
        assertEquals("Blood Test", result.getName());
    }

    @Test
    void delete_success() {
        when(clinicServiceRepository.findById(1L)).thenReturn(Optional.of(createService()));
        assertDoesNotThrow(() -> clinicServiceService.delete(1L));
        verify(clinicServiceRepository).deleteById(1L);
    }
}
