package com.myproject.clinic.serviceregistration.service;

import com.myproject.clinic.entity.ClinicService;
import com.myproject.clinic.entity.ServiceRegistration;
import com.myproject.clinic.entity.User;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.ClinicServiceRepository;
import com.myproject.clinic.repository.ServiceRegistrationRepository;
import com.myproject.clinic.repository.UserRepository;
import com.myproject.clinic.serviceregistration.dto.ServiceRegistrationRequest;
import com.myproject.clinic.serviceregistration.dto.ServiceRegistrationResponse;
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
class ServiceRegistrationServiceTest {

    @Mock
    private ServiceRegistrationRepository registrationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ClinicServiceRepository clinicServiceRepository;
    @InjectMocks
    private ServiceRegistrationService registrationService;

    private ServiceRegistration createRegistration() {
        User user = User.builder().id(1L).fullName("Patient").build();
        ClinicService service = ClinicService.builder().id(1L).name("Blood Test").build();
        return ServiceRegistration.builder().id(1L).user(user).service(service).status("PENDING").build();
    }

    @Test
    void findAll_returnsList() {
        when(registrationRepository.findAll()).thenReturn(List.of(createRegistration()));
        List<ServiceRegistrationResponse> result = registrationService.findAll();
        assertEquals(1, result.size());
        assertEquals("Patient", result.get(0).getUserName());
    }

    @Test
    void findById_found() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(createRegistration()));
        ServiceRegistrationResponse result = registrationService.findById(1L);
        assertEquals("Blood Test", result.getServiceName());
    }

    @Test
    void findById_notFound() {
        when(registrationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> registrationService.findById(99L));
    }

    @Test
    void create_success() {
        User user = User.builder().id(1L).fullName("Patient").build();
        ClinicService service = ClinicService.builder().id(1L).name("Blood Test").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(clinicServiceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(registrationRepository.save(any())).thenReturn(createRegistration());

        ServiceRegistrationRequest request = ServiceRegistrationRequest.builder()
                .userId(1L).serviceId(1L).build();
        ServiceRegistrationResponse result = registrationService.create(request);
        assertEquals("PENDING", result.getStatus());
    }

    @Test
    void delete_success() {
        when(registrationRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> registrationService.delete(1L));
        verify(registrationRepository).deleteById(1L);
    }
}
