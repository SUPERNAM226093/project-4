package com.myproject.clinic.prescription.service;

import com.myproject.clinic.entity.*;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.prescription.dto.*;
import com.myproject.clinic.repository.*;
import com.myproject.clinic.utils.EmailService;
import com.myproject.clinic.utils.SecurityUtils;
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
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private SecurityUtils securityUtils;
    @InjectMocks
    private PrescriptionService prescriptionService;

    private Prescription createPrescription() {
        Appointment appointment = Appointment.builder().id(1L).build();
        User doctorUser = User.builder().id(1L).fullName("Dr. Smith").build();
        Doctor doctor = Doctor.builder().id(1L).user(doctorUser).build();
        MedicalRecord record = MedicalRecord.builder().id(1L).appointment(appointment).doctor(doctor).build();
        return Prescription.builder().id(1L).medicalRecord(record).doctor(doctor)
                .items(new ArrayList<>()).build();
    }

    @Test
    void findAll_returnsList() {
        when(securityUtils.hasGlobalDataAccess()).thenReturn(true);
        when(prescriptionRepository.findAll()).thenReturn(List.of(createPrescription()));
        List<PrescriptionResponse> result = prescriptionService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void findById_found() {
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(createPrescription()));
        PrescriptionResponse result = prescriptionService.findById(1L);
        assertNotNull(result);
        assertEquals("Dr. Smith", result.getDoctorName());
        verify(securityUtils).assertDoctorOwnership("Prescription", 1L, 1L);
    }

    @Test
    void findById_notFound() {
        when(prescriptionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.findById(99L));
    }

    @Test
    void create_success() {
        Appointment appointment = Appointment.builder().id(1L).build();
        User doctorUser = User.builder().id(1L).fullName("Dr. Smith").build();
        Doctor doctor = Doctor.builder().id(1L).user(doctorUser).build();
        MedicalRecord record = MedicalRecord.builder().id(1L).appointment(appointment).doctor(doctor).build();

        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(securityUtils.resolveAndValidateDoctorId(1L)).thenReturn(1L);
        when(prescriptionRepository.save(any())).thenReturn(createPrescription());

        PrescriptionRequest request = PrescriptionRequest.builder()
                .medicalRecordId(1L).doctorId(1L)
                .items(List.of(PrescriptionItemRequest.builder()
                        .medicineName("Paracetamol").dosage("500mg").frequency("3x/day").build()))
                .build();
        PrescriptionResponse result = prescriptionService.create(request);
        assertNotNull(result);
        verify(emailService).sendPrescriptionEmail(any());
    }

    @Test
    void delete_success() {
        Prescription prescription = createPrescription();
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        assertDoesNotThrow(() -> prescriptionService.delete(1L));
        verify(prescriptionRepository).deleteById(1L);
        verify(securityUtils).assertDoctorOwnership("Prescription", 1L, 1L);
    }
}
