package com.myproject.clinic.auth.service;

import com.myproject.clinic.entity.User;
import com.myproject.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Lớp dịch vụ (Service) xử lý logic nghiệp vụ và dữ liệu cho thực thể CustomUserDetails.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Phương thức: Tải dữ liệu người dùng theo username.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String roleName = user.getRole() != null ? "ROLE_" + user.getRole().getName() : "ROLE_USER";

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName)));
    }
}
