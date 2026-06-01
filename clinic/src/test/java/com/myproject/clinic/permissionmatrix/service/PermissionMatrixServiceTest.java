package com.myproject.clinic.permissionmatrix.service;

import com.myproject.clinic.entity.PermissionSource;
import com.myproject.clinic.entity.Role;
import com.myproject.clinic.entity.RoleUrl;
import com.myproject.clinic.permissionmatrix.dto.ModuleActionsDto;
import com.myproject.clinic.permissionmatrix.dto.PermissionMatrixPutRequest;
import com.myproject.clinic.permissionmatrix.dto.PermissionMatrixResponse;
import com.myproject.clinic.repository.RoleRepository;
import com.myproject.clinic.repository.RoleUrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionMatrixServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RoleUrlRepository roleUrlRepository;
    @InjectMocks
    private PermissionMatrixService permissionMatrixService;

    private final Role staff = Role.builder().id(2L).name("STAFF").build();

    @Test
    void putMatrix_doctorsViewOnly_createsGetRow() {
        when(roleRepository.findById(2L)).thenReturn(Optional.of(staff));
        when(roleUrlRepository.findByRoleIdAndPermissionSource(2L, PermissionSource.MATRIX)).thenReturn(List.of());

        ModuleActionsDto doctors = ModuleActionsDto.builder().view(true).build();
        PermissionMatrixPutRequest request = new PermissionMatrixPutRequest(Map.of("doctors", doctors));

        when(roleUrlRepository.save(any())).thenAnswer(inv -> {
            RoleUrl r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        permissionMatrixService.putMatrix(2L, request);

        ArgumentCaptor<RoleUrl> captor = ArgumentCaptor.forClass(RoleUrl.class);
        verify(roleUrlRepository).save(captor.capture());
        RoleUrl saved = captor.getValue();
        assertEquals("/api/doctors/**", saved.getUrlPattern());
        assertEquals("GET", saved.getHttpMethod());
        assertEquals(PermissionSource.MATRIX, saved.getPermissionSource());
        assertEquals("doctors", saved.getMatrixModule());
        verify(roleUrlRepository).deleteMatrixModules(eq(2L), eq(PermissionSource.MATRIX), anyCollection());
    }

    @Test
    void putMatrix_manualRowNotDeleted() {
        when(roleRepository.findById(2L)).thenReturn(Optional.of(staff));
        when(roleUrlRepository.findByRoleIdAndPermissionSource(2L, PermissionSource.MATRIX)).thenReturn(List.of());

        PermissionMatrixPutRequest request = new PermissionMatrixPutRequest(
                Map.of("appointments", ModuleActionsDto.builder().view(true).build()));

        when(roleUrlRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PermissionMatrixResponse response = permissionMatrixService.putMatrix(2L, request);

        verify(roleUrlRepository, never()).deleteById(99L);
        assertTrue(response.getManualPermissions().isEmpty());
    }
}
