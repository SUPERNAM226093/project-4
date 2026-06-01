package com.myproject.clinic.healthpackage.service;

import com.myproject.clinic.config.FileStorageService;
import com.myproject.clinic.entity.HealthPackage;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.healthpackage.dto.HealthPackageRequest;
import com.myproject.clinic.healthpackage.dto.HealthPackageResponse;
import com.myproject.clinic.repository.AppointmentRepository;
import com.myproject.clinic.repository.HealthPackageBookingRepository;
import com.myproject.clinic.repository.HealthPackageRepository;
import com.myproject.clinic.utils.EmbeddingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthPackageServiceTest {

    @Mock
    private HealthPackageRepository healthPackageRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private HealthPackageBookingRepository healthPackageBookingRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private EmbeddingService embeddingService;
    @InjectMocks
    private HealthPackageService healthPackageService;

    private HealthPackage createPackage() {
        return HealthPackage.builder()
                .id(1L)
                .name("Gói khám tổng quát")
                .description("Khám sức khỏe tổng quát bao gồm xét nghiệm máu, siêu âm")
                .price(new BigDecimal("1500000"))
                .status("ACTIVE")
                .build();
    }

    private HealthPackage createPackageWithImage() {
        return HealthPackage.builder()
                .id(2L)
                .name("Gói khám tim mạch")
                .description("Khám chuyên sâu tim mạch")
                .price(new BigDecimal("3000000"))
                .status("ACTIVE")
                .featureImage("health-packages/heart-check.jpg")
                .build();
    }

    // --- findAll ---

    @Test
    void findAll_returnsList() {
        when(healthPackageRepository.findByStatus("ACTIVE")).thenReturn(List.of(createPackage()));
        List<HealthPackageResponse> result = healthPackageService.findAll();
        assertEquals(1, result.size());
        assertEquals("Gói khám tổng quát", result.get(0).getName());
    }

    @Test
    void findAll_emptyList() {
        when(healthPackageRepository.findByStatus("ACTIVE")).thenReturn(List.of());
        List<HealthPackageResponse> result = healthPackageService.findAll();
        assertTrue(result.isEmpty());
    }

    // --- findById ---

    @Test
    void findById_found() {
        when(healthPackageRepository.findById(1L)).thenReturn(Optional.of(createPackage()));
        HealthPackageResponse result = healthPackageService.findById(1L);
        assertEquals("Gói khám tổng quát", result.getName());
        assertEquals(new BigDecimal("1500000"), result.getPrice());
    }

    @Test
    void findById_notFound() {
        when(healthPackageRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> healthPackageService.findById(99L));
    }

    @Test
    void findById_withImage_returnsImageUrl() {
        when(healthPackageRepository.findById(2L)).thenReturn(Optional.of(createPackageWithImage()));
        HealthPackageResponse result = healthPackageService.findById(2L);
        assertEquals("/images/health-packages/heart-check.jpg", result.getFeatureImageUrl());
    }

    @Test
    void findById_withoutImage_returnsNullImageUrl() {
        when(healthPackageRepository.findById(1L)).thenReturn(Optional.of(createPackage()));
        HealthPackageResponse result = healthPackageService.findById(1L);
        assertNull(result.getFeatureImageUrl());
    }

    // --- create ---

    @Test
    void create_success() {
        when(healthPackageRepository.save(any())).thenReturn(createPackage());
        HealthPackageRequest request = HealthPackageRequest.builder()
                .name("Gói khám tổng quát")
                .description("Khám sức khỏe tổng quát bao gồm xét nghiệm máu, siêu âm")
                .price(new BigDecimal("1500000"))
                .build();
        HealthPackageResponse result = healthPackageService.create(request);
        assertEquals("Gói khám tổng quát", result.getName());
        verify(healthPackageRepository).save(any());
    }

    // --- update ---

    @Test
    void update_success() {
        HealthPackage existing = createPackage();
        when(healthPackageRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(healthPackageRepository.save(any())).thenReturn(existing);

        HealthPackageRequest request = HealthPackageRequest.builder()
                .name("Gói khám nâng cao")
                .price(new BigDecimal("2000000"))
                .build();
        HealthPackageResponse result = healthPackageService.update(1L, request);
        assertNotNull(result);
        verify(healthPackageRepository).save(any());
    }

    @Test
    void update_notFound() {
        when(healthPackageRepository.findById(99L)).thenReturn(Optional.empty());
        HealthPackageRequest request = HealthPackageRequest.builder().name("Test").build();
        assertThrows(ResourceNotFoundException.class, () -> healthPackageService.update(99L, request));
    }

    // --- delete ---

    @Test
    void delete_success() {
        when(healthPackageRepository.findById(1L)).thenReturn(Optional.of(createPackage()));
        when(appointmentRepository.findByHealthPackageId(1L)).thenReturn(new ArrayList<>());
        assertDoesNotThrow(() -> healthPackageService.delete(1L));
        verify(healthPackageRepository).deleteById(1L);
    }

    @Test
    void delete_withImage_deletesFile() {
        when(healthPackageRepository.findById(2L)).thenReturn(Optional.of(createPackageWithImage()));
        when(appointmentRepository.findByHealthPackageId(2L)).thenReturn(new ArrayList<>());
        healthPackageService.delete(2L);
        verify(fileStorageService).delete("health-packages/heart-check.jpg");
        verify(healthPackageRepository).deleteById(2L);
    }

    @Test
    void delete_notFound() {
        when(healthPackageRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> healthPackageService.delete(99L));
    }

    // --- uploadFeatureImage ---

    @Test
    void uploadFeatureImage_success() {
        HealthPackage hp = createPackage();
        when(healthPackageRepository.findById(1L)).thenReturn(Optional.of(hp));
        when(fileStorageService.store(any(), eq("health-packages"))).thenReturn("health-packages/new-image.jpg");
        when(healthPackageRepository.save(any())).thenReturn(hp);

        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "test".getBytes());
        HealthPackageResponse result = healthPackageService.uploadFeatureImage(1L, file);
        assertNotNull(result);
        verify(fileStorageService).store(any(), eq("health-packages"));
        verify(healthPackageRepository).save(any());
    }

    @Test
    void uploadFeatureImage_replacesExisting() {
        HealthPackage hp = createPackageWithImage();
        when(healthPackageRepository.findById(2L)).thenReturn(Optional.of(hp));
        when(fileStorageService.store(any(), eq("health-packages"))).thenReturn("health-packages/replacement.jpg");
        when(healthPackageRepository.save(any())).thenReturn(hp);

        MockMultipartFile file = new MockMultipartFile("file", "new.jpg", "image/jpeg", "test".getBytes());
        healthPackageService.uploadFeatureImage(2L, file);
        verify(fileStorageService).delete("health-packages/heart-check.jpg");
        verify(fileStorageService).store(any(), eq("health-packages"));
    }

    @Test
    void uploadFeatureImage_notFound() {
        when(healthPackageRepository.findById(99L)).thenReturn(Optional.empty());
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "test".getBytes());
        assertThrows(ResourceNotFoundException.class, () -> healthPackageService.uploadFeatureImage(99L, file));
    }

    // --- deleteFeatureImage ---

    @Test
    void deleteFeatureImage_success() {
        HealthPackage hp = createPackageWithImage();
        when(healthPackageRepository.findById(2L)).thenReturn(Optional.of(hp));
        when(healthPackageRepository.save(any())).thenReturn(hp);

        HealthPackageResponse result = healthPackageService.deleteFeatureImage(2L);
        assertNotNull(result);
        verify(fileStorageService).delete("health-packages/heart-check.jpg");
    }

    @Test
    void deleteFeatureImage_noImage_doesNothing() {
        HealthPackage hp = createPackage();
        when(healthPackageRepository.findById(1L)).thenReturn(Optional.of(hp));

        HealthPackageResponse result = healthPackageService.deleteFeatureImage(1L);
        assertNotNull(result);
        verify(fileStorageService, never()).delete(any());
    }

    @Test
    void deleteFeatureImage_notFound() {
        when(healthPackageRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> healthPackageService.deleteFeatureImage(99L));
    }
}
