package com.myproject.clinic.role.service;

import com.myproject.clinic.entity.Role;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.RoleRepository;
import com.myproject.clinic.repository.RoleUrlRepository;
import com.myproject.clinic.repository.UserRepository;
import com.myproject.clinic.role.dto.RoleRequest;
import com.myproject.clinic.role.dto.RoleResponse;
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
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleUrlRepository roleUrlRepository;
    @InjectMocks
    private RoleService roleService;

    @Test
    void findAll_returnsList() {
        when(roleRepository.findAll()).thenReturn(List.of(
                Role.builder().id(1L).name("ADMIN").build(),
                Role.builder().id(2L).name("PATIENT").build()
        ));

        List<RoleResponse> result = roleService.findAll();
        assertEquals(2, result.size());
        assertEquals("ADMIN", result.get(0).getName());
    }

    @Test
    void findById_found() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(Role.builder().id(1L).name("ADMIN").build()));
        RoleResponse result = roleService.findById(1L);
        assertEquals("ADMIN", result.getName());
    }

    @Test
    void findById_notFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.findById(99L));
    }

    @Test
    void create_success() {
        when(roleRepository.save(any())).thenReturn(Role.builder().id(1L).name("DOCTOR").build());
        RoleResponse result = roleService.create(new RoleRequest("DOCTOR", true));
        assertEquals("DOCTOR", result.getName());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void update_success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(Role.builder().id(1L).name("OLD").build()));
        when(roleRepository.save(any())).thenReturn(Role.builder().id(1L).name("NEW").build());

        RoleResponse result = roleService.update(1L, new RoleRequest("NEW", true));
        assertEquals("NEW", result.getName());
    }

    @Test
    void delete_success() {
        when(roleRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsByRoleId(1L)).thenReturn(false);
        when(roleUrlRepository.findByRoleId(1L)).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> roleService.delete(1L));
        verify(roleRepository).deleteById(1L);
    }

    @Test
    void delete_notFound() {
        when(roleRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> roleService.delete(99L));
    }
}
