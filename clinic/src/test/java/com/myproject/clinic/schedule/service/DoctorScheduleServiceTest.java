package com.myproject.clinic.schedule.service;

import com.myproject.clinic.entity.Doctor;
import com.myproject.clinic.entity.DoctorSchedule;
import com.myproject.clinic.entity.User;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.AppointmentRepository;
import com.myproject.clinic.repository.DoctorRepository;
import com.myproject.clinic.repository.DoctorScheduleRepository;
import com.myproject.clinic.schedule.dto.DoctorScheduleRequest;
import com.myproject.clinic.schedule.dto.DoctorScheduleResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleServiceTest {

    @Mock
    private DoctorScheduleRepository scheduleRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @InjectMocks
    private DoctorScheduleService scheduleService;

    private DoctorSchedule createSchedule() {
        User user = User.builder().id(1L).fullName("Dr. Smith").build();
        Doctor doctor = Doctor.builder().id(1L).user(user).build();
        return DoctorSchedule.builder().id(1L).doctor(doctor)
                .workDate(LocalDate.of(2026, 3, 1))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(17, 0)).build();
    }

    @Test
    void findAll_returnsList() {
        when(scheduleRepository.findAll()).thenReturn(List.of(createSchedule()));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
                any(), any(), any(), any())).thenReturn(false);
        List<DoctorScheduleResponse> result = scheduleService.findAll();
        assertEquals(1, result.size());
        assertEquals("Dr. Smith", result.get(0).getDoctorName());
    }

    @Test
    void findById_found() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(createSchedule()));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
                any(), any(), any(), any())).thenReturn(false);
        DoctorScheduleResponse result = scheduleService.findById(1L);
        assertEquals(LocalDate.of(2026, 3, 1), result.getWorkDate());
    }

    @Test
    void findById_notFound() {
        when(scheduleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> scheduleService.findById(99L));
    }

    @Test
    void create_success() {
        User user = User.builder().id(1L).fullName("Dr. Smith").build();
        Doctor doctor = Doctor.builder().id(1L).user(user).build();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(scheduleRepository.save(any())).thenReturn(createSchedule());
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
                any(), any(), any(), any())).thenReturn(false);

        DoctorScheduleRequest request = DoctorScheduleRequest.builder()
                .doctorId(1L).workDate(LocalDate.of(2026, 3, 1))
                .startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(17, 0)).build();
        DoctorScheduleResponse result = scheduleService.create(request);
        assertNotNull(result);
    }

    @Test
    void delete_success() {
        DoctorSchedule schedule = createSchedule();
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(appointmentRepository.findByScheduleId(1L)).thenReturn(new ArrayList<>());
        assertDoesNotThrow(() -> scheduleService.delete(1L));
        verify(scheduleRepository).delete(schedule);
    }
}
