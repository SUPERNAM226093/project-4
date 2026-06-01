package com.myproject.clinic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Cấu hình bảo mật Spring Security (SecurityConfig).
 * Kích hoạt các tính năng bảo mật web, phân quyền API, tích hợp bộ lọc JWT, CORS, và mã hóa mật khẩu.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Bộ lọc giải mã và xác thực token JWT
    private final RoleUrlAuthorizationFilter roleUrlAuthorizationFilter; // Bộ lọc kiểm tra phân quyền động theo URL/HTTP Method
    private final JwtAuthEntryPoint jwtAuthEntryPoint; // Lớp xử lý lỗi chưa đăng nhập (401 Unauthorized)
    private final UserDetailsService userDetailsService; // Service truy vấn thông tin tài khoản người dùng

    /**
     * Cấu hình chuỗi bộ lọc bảo mật chính của Spring Security (SecurityFilterChain).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Kích hoạt cấu hình CORS (chia sẻ tài nguyên giữa các nguồn khác nhau)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // 2. Vô hiệu hóa CSRF (Cross-Site Request Forgery) vì ứng dụng sử dụng cơ chế xác thực Stateless với Token JWT
                .csrf(csrf -> csrf.disable())
                
                // 3. Cấu hình xử lý lỗi ngoại lệ bảo mật
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
                
                // 4. Thiết lập chính sách Session là STATELESS (Không lưu trạng thái session trên Server)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 5. Cấu hình phân quyền truy cập cho các Endpoint cụ thể
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập tự do tới các API xác thực, hình ảnh tĩnh, các endpoint công khai
                        .requestMatchers("/api/auth/**", "/images/**", "/rooms/**", "/api/role-urls/my-permissions",
                                "/api/appointments/by-patient/**", "/api/medical-records/by-patient/**",
                                "/api/health-package-bookings/patient/**", "/api/online-consultations/patient/**",
                                "/api/room-bookings/by-user/**", "/api/prescriptions/patient/**")
                        .permitAll()

                        // Quyền truy cập tự do cho model dự đoán bệnh tim và API chat AI
                        .requestMatchers("/api/machine-learning/heart-disease/predict").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/chat").permitAll()
                        
                        // Cho phép xem (GET) danh sách bác sĩ, dịch vụ, phòng bệnh, chuyên khoa, gói khám, bệnh viện công khai
                        .requestMatchers(HttpMethod.GET, "/api/doctors", "/api/doctors/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/services", "/api/services/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rooms", "/api/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/specializations", "/api/specializations/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/health-packages", "/api/health-packages/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hospitals", "/api/hospitals/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/login", "/api/login/**", "/api/register").permitAll()
                        
                        // Các API đặt lịch khám và gói dịch vụ trực tuyến công khai hoặc có bộ lọc phân quyền động xử lý sau
                        .requestMatchers(HttpMethod.POST, "/api/health-package-bookings").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/health-package-bookings/booked-slots/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/health-package-bookings/*/cancel").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/online-consultations").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/online-consultations/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/online-consultations/*/cancel").permitAll()
                        
                        // Chỉ vai trò ADMIN mới được quản lý ma trận quyền truy cập
                        .requestMatchers("/api/roles/*/permission-matrix", "/api/permission-matrix/**").hasRole("ADMIN")

                        // Tất cả các request còn lại đều bắt buộc phải xác thực (đăng nhập).
                        // Phân quyền chi tiết cho các Role (bao gồm Role tự tạo) sẽ do RoleUrlAuthorizationFilter đảm nhận.
                        .anyRequest().authenticated())
                
                // 6. Cung cấp bộ quản lý xác thực tài khoản và mã hóa mật khẩu
                .authenticationProvider(authenticationProvider())
                
                // 7. Chèn JwtAuthenticationFilter vào trước bộ lọc UsernamePasswordAuthenticationFilter mặc định của Spring
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                
                // 8. Chèn RoleUrlAuthorizationFilter chạy ngay sau JwtAuthenticationFilter để kiểm tra phân quyền URL động
                .addFilterAfter(roleUrlAuthorizationFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Cấu hình CORS (Cross-Origin Resource Sharing).
     * Cho phép các cổng chạy của Frontend (NextJS, Vite) gọi API sang Backend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Cấu hình danh sách các tên miền / cổng Frontend được phép gọi tới Backend
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Cho phép gửi kèm thông tin xác thực như Cookie/Credentials
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cấu hình CORS cho tất cả endpoint
        return source;
    }

    /**
     * Khởi tạo AuthenticationProvider.
     * Liên kết dữ liệu tài khoản người dùng và bộ mã hóa mật khẩu để kiểm tra thông tin đăng nhập.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // Cung cấp lớp truy vấn tài khoản người dùng
        provider.setPasswordEncoder(passwordEncoder()); // Cung cấp thuật toán mã hóa BCrypt
        return provider;
    }

    /**
     * Bean quản lý xác thực AuthenticationManager của Spring Security.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Sử dụng thuật toán BCrypt để băm mật khẩu thô một cách an toàn trước khi lưu vào DB.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
