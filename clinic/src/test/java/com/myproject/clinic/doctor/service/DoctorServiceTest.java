package com.myproject.clinic.doctor.service;

import com.myproject.clinic.config.FileStorageService;
import com.myproject.clinic.doctor.dto.DoctorRequest;
import com.myproject.clinic.doctor.dto.DoctorResponse;
import com.myproject.clinic.entity.Doctor;
import com.myproject.clinic.entity.Specialization;
import com.myproject.clinic.entity.User;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.*;
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
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private PrescriptionRepository prescriptionRepository;
    @Mock
    private OnlineConsultationRepository onlineConsultationRepository;
    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private EmbeddingService embeddingService;
    @InjectMocks
    private DoctorService doctorService;

    private Doctor createDoctor() {
        User user = User.builder().id(1L).email("doc@test.com").fullName("Dr. Test").build();
        Specialization spec = Specialization.builder().id(1L).name("Cardiology").build();
        return Doctor.builder().id(1L).user(user).specialization(spec)
                .clinicId(1L).experienceYears(10).build();
    }

    @Test
    void findAll_returnsList() {
        when(doctorRepository.findByUser_Status("ACTIVE")).thenReturn(List.of(createDoctor()));
        List<DoctorResponse> result = doctorService.findAll();
        assertEquals(1, result.size());
        assertEquals("Dr. Test", result.get(0).getFullName());
    }

    @Test
    void findById_found() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(createDoctor()));
        DoctorResponse result = doctorService.findById(1L);
        assertEquals("Dr. Test", result.getFullName());
        assertEquals("Cardiology", result.getSpecializationName());
    }

    @Test
    void findById_notFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> doctorService.findById(99L));
    }

    @Test
    void create_success() {
        User user = User.builder().id(1L).email("doc@test.com").fullName("Dr. Test").build();
        Specialization spec = Specialization.builder().id(1L).name("Cardiology").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(spec));
        when(doctorRepository.save(any())).thenReturn(createDoctor());

        DoctorRequest request = DoctorRequest.builder().userId(1L).specializationId(1L)
                .clinicId(1L).experienceYears(10).build();
        DoctorResponse result = doctorService.create(request);
        assertEquals(1L, result.getClinicId());
    }

    @Test
    void delete_success() {
        Doctor doctor = createDoctor();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorId(1L)).thenReturn(new ArrayList<>());
        when(medicalRecordRepository.findByDoctorId(1L)).thenReturn(new ArrayList<>());
        when(prescriptionRepository.findByDoctorIdOrderByCreatedAtDesc(1L)).thenReturn(new ArrayList<>());
        when(onlineConsultationRepository.existsByDoctorId(1L)).thenReturn(false);
        when(doctorScheduleRepository.findByDoctorId(1L)).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> doctorService.delete(1L));
        verify(doctorRepository).deleteById(1L);
    }
}
