package com.myproject.clinic.medicalrecord.service;

import com.myproject.clinic.entity.*;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.medicalrecord.dto.MedicalRecordRequest;
import com.myproject.clinic.medicalrecord.dto.MedicalRecordResponse;
import com.myproject.clinic.repository.*;
import com.myproject.clinic.utils.EmailService;
import com.myproject.clinic.utils.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private SecurityUtils securityUtils;
    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private MedicalRecord createRecord() {
        Appointment appointment = Appointment.builder().id(1L).build();
        User doctorUser = User.builder().id(1L).fullName("Dr. Smith").build();
        Doctor doctor = Doctor.builder().id(1L).user(doctorUser).build();
        return MedicalRecord.builder().id(1L).appointment(appointment).doctor(doctor)
                .diagnosis("Flu").conclusion("Rest").build();
    }

    @Test
    void findAll_returnsList() {
        when(securityUtils.hasGlobalDataAccess()).thenReturn(true);
        when(medicalRecordRepository.findAll()).thenReturn(List.of(createRecord()));
        List<MedicalRecordResponse> result = medicalRecordService.findAll();
        assertEquals(1, result.size());
        assertEquals("Flu", result.get(0).getDiagnosis());
    }

    @Test
    void findById_found() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(createRecord()));
        MedicalRecordResponse result = medicalRecordService.findById(1L);
        assertEquals("Flu", result.getDiagnosis());
    }

    @Test
    void findById_notFound() {
        when(medicalRecordRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> medicalRecordService.findById(99L));
    }

    @Test
    void create_success() {
        Appointment appointment = Appointment.builder().id(1L).build();
        User doctorUser = User.builder().id(1L).fullName("Dr. Smith").build();
        Doctor doctor = Doctor.builder().id(1L).user(doctorUser).build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(securityUtils.resolveAndValidateDoctorId(1L)).thenReturn(1L);
        when(medicalRecordRepository.save(any())).thenReturn(createRecord());

        MedicalRecordRequest request = MedicalRecordRequest.builder()
                .appointmentId(1L).doctorId(1L).diagnosis("Flu").conclusion("Rest").build();
        MedicalRecordResponse result = medicalRecordService.create(request);
        assertNotNull(result);
        verify(emailService).sendMedicalRecordEmail(any());
    }

    @Test
    void delete_success() {
        MedicalRecord record = createRecord();
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(record));
        assertDoesNotThrow(() -> medicalRecordService.delete(1L));
        verify(medicalRecordRepository).delete(record);
    }
}
