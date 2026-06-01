package com.myproject.clinic.healthpackage.service;

import com.myproject.clinic.entity.HealthPackage;
import com.myproject.clinic.entity.HealthPackageSchedule;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.healthpackage.dto.HealthPackageScheduleRequest;
import com.myproject.clinic.healthpackage.dto.HealthPackageScheduleResponse;
import com.myproject.clinic.repository.HealthPackageRepository;
import com.myproject.clinic.repository.HealthPackageScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthPackageScheduleServiceTest {

    @Mock
    private HealthPackageScheduleRepository scheduleRepository;
    @Mock
    private HealthPackageRepository healthPackageRepository;
    @InjectMocks
    private HealthPackageScheduleService scheduleService;

    private HealthPackage createPackage() {
        return HealthPackage.builder().id(1L).name("Gói khám tổng quát").build();
    }

    private HealthPackageSchedule createSchedule() {
        return HealthPackageSchedule.builder()
                .id(1L)
                .healthPackage(createPackage())
                .workDate(LocalDate.of(2026, 4, 1))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
    }

    // --- findAll ---

    @Test
    void findAll_returnsList() {
        when(scheduleRepository.findAll()).thenReturn(List.of(createSchedule()));
        List<HealthPackageScheduleResponse> result = scheduleService.findAll();
        assertEquals(1, result.size());
        assertEquals("Gói khám tổng quát", result.get(0).getHealthPackageName());
    }

    @Test
    void findAll_emptyList() {
        when(scheduleRepository.findAll()).thenReturn(List.of());
        List<HealthPackageScheduleResponse> result = scheduleService.findAll();
        assertTrue(result.isEmpty());
    }

    // --- findById ---

    @Test
    void findById_found() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(createSchedule()));
        HealthPackageScheduleResponse result = scheduleService.findById(1L);
        assertEquals(LocalDate.of(2026, 4, 1), result.getWorkDate());
        assertEquals(LocalTime.of(8, 0), result.getStartTime());
        assertEquals(LocalTime.of(12, 0), result.getEndTime());
    }

    @Test
    void findById_notFound() {
        when(scheduleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> scheduleService.findById(99L));
    }

    // --- findByHealthPackageId ---

    @Test
    void findByHealthPackageId_returnsList() {
        when(scheduleRepository.findByHealthPackageId(1L)).thenReturn(List.of(createSchedule()));
        List<HealthPackageScheduleResponse> result = scheduleService.findByHealthPackageId(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getHealthPackageId());
    }

    @Test
    void findByHealthPackageId_emptyList() {
        when(scheduleRepository.findByHealthPackageId(99L)).thenReturn(List.of());
        List<HealthPackageScheduleResponse> result = scheduleService.findByHealthPackageId(99L);
        assertTrue(result.isEmpty());
    }

    // --- create ---

    @Test
    void create_success() {
        HealthPackage hp = createPackage();
        when(healthPackageRepository.findById(1L)).thenReturn(Optional.of(hp));
        when(scheduleRepository.save(any())).thenReturn(createSchedule());

        HealthPackageScheduleRequest request = HealthPackageScheduleRequest.builder()
                .healthPackageId(1L)
                .workDate(LocalDate.of(2026, 4, 1))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
        HealthPackageScheduleResponse result = scheduleService.create(request);
        assertNotNull(result);
        assertEquals(LocalDate.of(2026, 4, 1), result.getWorkDate());
        verify(scheduleRepository).save(any());
    }

    @Test
    void create_packageNotFound() {
        when(healthPackageRepository.findById(99L)).thenReturn(Optional.empty());
        HealthPackageScheduleRequest request = HealthPackageScheduleRequest.builder()
                .healthPackageId(99L)
                .workDate(LocalDate.of(2026, 4, 1))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
        assertThrows(ResourceNotFoundException.class, () -> scheduleService.create(request));
    }

    // --- update ---

    @Test
    void update_success() {
        HealthPackageSchedule existing = createSchedule();
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(scheduleRepository.save(any())).thenReturn(existing);

        HealthPackageScheduleRequest request = HealthPackageScheduleRequest.builder()
                .workDate(LocalDate.of(2026, 4, 15))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .build();
        HealthPackageScheduleResponse result = scheduleService.update(1L, request);
        assertNotNull(result);
        verify(scheduleRepository).save(any());
    }

    @Test
    void update_notFound() {
        when(scheduleRepository.findById(99L)).thenReturn(Optional.empty());
        HealthPackageScheduleRequest request = HealthPackageScheduleRequest.builder()
                .workDate(LocalDate.of(2026, 4, 15))
                .build();
        assertThrows(ResourceNotFoundException.class, () -> scheduleService.update(99L, request));
    }

    // --- delete ---

    @Test
    void delete_success() {
        when(scheduleRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> scheduleService.delete(1L));
        verify(scheduleRepository).deleteById(1L);
    }

    @Test
    void delete_notFound() {
        when(scheduleRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> scheduleService.delete(99L));
    }

    // --- response mapping ---

    @Test
    void toResponse_mapsCorrectly() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(createSchedule()));
        HealthPackageScheduleResponse result = scheduleService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals(1L, result.getHealthPackageId());
        assertEquals("Gói khám tổng quát", result.getHealthPackageName());
        assertEquals(LocalDate.of(2026, 4, 1), result.getWorkDate());
        assertEquals(LocalTime.of(8, 0), result.getStartTime());
        assertEquals(LocalTime.of(12, 0), result.getEndTime());
    }
}
