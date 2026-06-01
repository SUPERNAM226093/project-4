package com.myproject.clinic.utils;

import com.myproject.clinic.entity.Doctor;
import com.myproject.clinic.entity.Role;
import com.myproject.clinic.entity.User;
import com.myproject.clinic.repository.DoctorRepository;
import com.myproject.clinic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityUtilsTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void mockAuthenticatedUser(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null)
        );
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    private User buildUser(Long id, String email, String roleName) {
        Role role = new Role(1L, roleName, true);
        return User.builder().id(id).email(email).role(role).build();
    }

    // ── TC-04: DOCTOR xem đúng dữ liệu của mình ──────────────────────────────────

    @Test
    @DisplayName("TC-04: DOCTOR has valid Doctor profile → getCurrentDoctorOrNull returns Doctor")
    void getCurrentDoctorOrNull_doctorRole_profileExists_returnsDoctor() {
        User user = buildUser(10L, "doctor@clinic.com", "DOCTOR");
        Doctor doctor = Doctor.builder().id(100L).user(user).build();

        mockAuthenticatedUser(user);
        when(doctorRepository.findByUserId(10L)).thenReturn(Optional.of(doctor));

        Doctor result = securityUtils.getCurrentDoctorOrNull();
        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    // ── TC-05: DOCTOR role nhưng không có Doctor entity → 403 ────────────────────

    @Test
    @DisplayName("TC-05: DOCTOR role but no Doctor entity in DB → 403 Forbidden")
    void getCurrentDoctorOrNull_doctorRole_noProfileInDB_throws403() {
        User user = buildUser(10L, "doctor@clinic.com", "DOCTOR");

        mockAuthenticatedUser(user);
        when(doctorRepository.findByUserId(10L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> securityUtils.getCurrentDoctorOrNull());
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    // ── TC-03: STAFF gọi → getCurrentDoctorOrNull trả null (không bị chặn) ───────

    @Test
    @DisplayName("TC-03: STAFF role → getCurrentDoctorOrNull returns null (no restriction)")
    void getCurrentDoctorOrNull_staffRole_returnsNull() {
        User user = buildUser(20L, "staff@clinic.com", "STAFF");
        mockAuthenticatedUser(user);

        Doctor result = securityUtils.getCurrentDoctorOrNull();
        assertNull(result);
        // DoctorRepository không được gọi cho STAFF
        verifyNoInteractions(doctorRepository);
    }

    // ── TC-01: DOCTOR A cố truy cập resource của DOCTOR B → 403 ─────────────────

    @Test
    @DisplayName("TC-01: DOCTOR_A accesses resource owned by DOCTOR_B → 403 Forbidden")
    void assertDoctorOwnership_differentDoctorId_throws403() {
        User user = buildUser(10L, "doctorA@clinic.com", "DOCTOR");
        Doctor doctorA = Doctor.builder().id(100L).user(user).build();

        mockAuthenticatedUser(user);
        when(doctorRepository.findByUserId(10L)).thenReturn(Optional.of(doctorA));

        Long ownerDoctorBId = 200L; // resource thuộc về Doctor B
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> securityUtils.assertDoctorOwnership("Appointment", 999L, ownerDoctorBId));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    @DisplayName("TC-01 (pass): DOCTOR_A accesses own resource → no exception")
    void assertDoctorOwnership_sameDoctorId_noException() {
        User user = buildUser(10L, "doctorA@clinic.com", "DOCTOR");
        Doctor doctorA = Doctor.builder().id(100L).user(user).build();

        mockAuthenticatedUser(user);
        when(doctorRepository.findByUserId(10L)).thenReturn(Optional.of(doctorA));

        // Không throw khi doctorId khớp
        assertDoesNotThrow(() -> securityUtils.assertDoctorOwnership("Appointment", 1L, 100L));
    }

    @Test
    @DisplayName("TC-03: STAFF calls assertDoctorOwnership → no exception (global access)")
    void assertDoctorOwnership_staffRole_noException() {
        User user = buildUser(20L, "staff@clinic.com", "STAFF");
        mockAuthenticatedUser(user);

        // STAFF không bị kiểm tra ownership → không throw
        assertDoesNotThrow(() -> securityUtils.assertDoctorOwnership("Appointment", 1L, 999L));
    }

    // ── TC-02: DOCTOR truyền doctorId của B trong body → bị log, ID bị override ──

    @Test
    @DisplayName("TC-02: DOCTOR sends spoofed doctorId in body → returns own doctorId")
    void resolveAndValidateDoctorId_spoofedId_returnsOwnId() {
        User user = buildUser(10L, "doctorA@clinic.com", "DOCTOR");
        Doctor doctorA = Doctor.builder().id(100L).user(user).build();

        mockAuthenticatedUser(user);
        when(doctorRepository.findByUserId(10L)).thenReturn(Optional.of(doctorA));

        Long spoofedDoctorBId = 200L;
        Long result = securityUtils.resolveAndValidateDoctorId(spoofedDoctorBId);

        // Kết quả phải là ID của chính bác sĩ A, không phải B
        assertEquals(100L, result);
    }

    @Test
    @DisplayName("TC-02 (staff): STAFF passes doctorId → value is kept as-is")
    void resolveAndValidateDoctorId_staffRole_keepOriginalId() {
        User user = buildUser(20L, "staff@clinic.com", "STAFF");
        mockAuthenticatedUser(user);

        Long result = securityUtils.resolveAndValidateDoctorId(999L);
        assertEquals(999L, result);
    }

    // ── TC-07: Token hết hạn / anonymous → 401 ───────────────────────────────────

    @Test
    @DisplayName("TC-07: No user in DB (expired/invalid token) → 401 Unauthorized")
    void getCurrentUser_userNotFound_throws401() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("ghost@clinic.com", null)
        );
        when(userRepository.findByEmail("ghost@clinic.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> securityUtils.getCurrentUser());
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}
