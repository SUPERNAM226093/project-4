package com.myproject.clinic.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.clinic.auth.dto.AuthResponse;
import com.myproject.clinic.auth.dto.LoginRequest;
import com.myproject.clinic.auth.dto.RegisterRequest;
import com.myproject.clinic.auth.service.AuthService;
import com.myproject.clinic.config.JwtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private AuthService authService;
        @MockitoBean
        private JwtConfig jwtConfig;


        @Test
        void register_returnsToken() throws Exception {
                RegisterRequest request = RegisterRequest.builder()
                                .email("test@example.com").password("password123").fullName("Test").build();
                AuthResponse response = AuthResponse.builder()
                                .token("jwt-token").email("test@example.com").fullName("Test").role("PATIENT").build();

                when(authService.register(any())).thenReturn(response);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("jwt-token"))
                                .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        void login_returnsToken() throws Exception {
                LoginRequest request = LoginRequest.builder().email("test@example.com").password("pass123").build();
                AuthResponse response = AuthResponse.builder()
                                .token("jwt-token").email("test@example.com").role("PATIENT").build();

                when(authService.login(any())).thenReturn(response);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("jwt-token"));
        }
}
