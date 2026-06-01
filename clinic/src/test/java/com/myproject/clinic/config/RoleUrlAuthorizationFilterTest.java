package com.myproject.clinic.config;

import com.myproject.clinic.entity.Role;
import com.myproject.clinic.entity.RoleUrl;
import com.myproject.clinic.repository.RoleRepository;
import com.myproject.clinic.repository.RoleUrlRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleUrlAuthorizationFilterTest {

    @Mock
    private JwtConfig jwtConfig;
    @Mock
    private RoleUrlRepository roleUrlRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private FilterChain filterChain;

    private RoleUrlAuthorizationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RoleUrlAuthorizationFilter(jwtConfig, roleUrlRepository, roleRepository);
    }

    @Test
    void staffPostAppointments_withoutPermission_returns403() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/appointments");
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtConfig.extractRole("token")).thenReturn("STAFF");
        when(roleRepository.findByName("STAFF")).thenReturn(Optional.of(Role.builder().id(2L).name("STAFF").isActive(true).build()));
        when(roleUrlRepository.findByRoleNameAndHttpMethod("STAFF", "POST")).thenReturn(List.of());

        filter.doFilter(request, response, filterChain);

        assertEquals(403, response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void staffGetAppointments_withPermission_continuesChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/appointments");
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Role staffRole = Role.builder().id(2L).name("STAFF").isActive(true).build();
        when(jwtConfig.extractRole("token")).thenReturn("STAFF");
        when(roleRepository.findByName("STAFF")).thenReturn(Optional.of(staffRole));
        when(roleUrlRepository.findByRoleNameAndHttpMethod("STAFF", "GET")).thenReturn(List.of(
                RoleUrl.builder().role(staffRole).urlPattern("/api/appointments/**").httpMethod("GET").build()));

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void staffGetAppointments_withInactiveRole_returns403() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/appointments");
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Role staffRole = Role.builder().id(2L).name("STAFF").isActive(false).build();
        when(jwtConfig.extractRole("token")).thenReturn("STAFF");
        when(roleRepository.findByName("STAFF")).thenReturn(Optional.of(staffRole));

        filter.doFilter(request, response, filterChain);

        assertEquals(403, response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void unauthenticatedGetDoctors_continuesChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/doctors");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(roleUrlRepository, never()).findByRoleNameAndHttpMethod(anyString(), anyString());
    }
}
