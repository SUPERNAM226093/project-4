package com.myproject.clinic.roleurl.service;

import com.myproject.clinic.entity.Role;
import com.myproject.clinic.entity.RoleUrl;
import com.myproject.clinic.repository.RoleUrlRepository;
import com.myproject.clinic.roleurl.dto.RoleUrlResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleUrlServiceTest {

    @Mock
    private RoleUrlRepository roleUrlRepository;
    @InjectMocks
    private RoleUrlService roleUrlService;

    private final Role adminRole = Role.builder().id(1L).name("ADMIN").build();

    @Test
    void findByRoleName_returnsList() {
        when(roleUrlRepository.findByRoleName("ADMIN")).thenReturn(List.of(
                RoleUrl.builder().id(1L).role(adminRole).urlPattern("/users").httpMethod("GET").build(),
                RoleUrl.builder().id(2L).role(adminRole).urlPattern("/roles").httpMethod("POST").build()
        ));

        List<RoleUrlResponse> result = roleUrlService.findByRoleName("ADMIN");
        assertEquals(2, result.size());
        assertEquals("/users", result.get(0).getUrlPattern());
        assertEquals("GET", result.get(0).getHttpMethod());
    }
}
