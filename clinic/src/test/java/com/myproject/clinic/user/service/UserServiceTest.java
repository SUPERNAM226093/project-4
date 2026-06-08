package com.myproject.clinic.user.service;

import com.myproject.clinic.entity.User;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.DoctorRepository;
import com.myproject.clinic.repository.UserRepository;
import com.myproject.clinic.user.dto.UserRequest;
import com.myproject.clinic.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    private User createUser() {
        return User.builder().id(1L).email("test@gmail.com").passwordHash("hashed")
                .fullName("Test User").roleName("PATIENT").status("ACTIVE").build();
    }

    @Test
    void findAll_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(createUser()));
        List<UserResponse> result = userService.findAll();
        assertEquals(1, result.size());
        assertEquals("test@gmail.com", result.get(0).getEmail());
    }

    @Test
    void findById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser()));
        UserResponse result = userService.findById(1L);
        assertEquals("Test User", result.getFullName());
    }

    @Test
    void findById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    void create_success() {
        UserRequest request = UserRequest.builder().email("new@gmail.com").password("pass").roleName("PATIENT").build();
        User saved = User.builder().id(2L).email("new@gmail.com").passwordHash("enc").roleName("PATIENT").status("ACTIVE")
                .build();

        when(passwordEncoder.encode("pass")).thenReturn("enc");
        when(userRepository.save(any())).thenReturn(saved);

        UserResponse result = userService.create(request);
        assertEquals("new@gmail.com", result.getEmail());
    }

    @Test
    void update_success() {
        User user = createUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserRequest request = UserRequest.builder().fullName("Updated Name").build();
        UserResponse result = userService.update(1L, request);
        assertNotNull(result);
    }

    @Test
    void delete_success() {
        User user = createUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userService.delete(1L));
        verify(userRepository).save(user);
        assertEquals("DELETED", user.getStatus());
    }
}
