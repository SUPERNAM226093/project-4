package com.myproject.clinic.config;

import com.myproject.clinic.entity.RoleUrl;
import com.myproject.clinic.repository.RoleUrlRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Bộ lọc phân quyền động theo URL và Phương thức HTTP (RoleUrlAuthorizationFilter).
 * Kế thừa OncePerRequestFilter để kiểm soát chi tiết quyền truy cập API của từng Vai trò (Role).
 * Kiểm tra xem người dùng hiện tại có vai trò tương ứng được phép truy cập vào URL đích thông qua phương thức HTTP yêu cầu không.
 */
@Component
@RequiredArgsConstructor
public class RoleUrlAuthorizationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig; // Lớp xử lý Token JWT
    private final RoleUrlRepository roleUrlRepository; // Repository lấy danh sách phân quyền từ DB
    private final com.myproject.clinic.repository.RoleRepository roleRepository; // Kiểm tra trạng thái Role
    private final AntPathMatcher antPathMatcher = new AntPathMatcher(); // Bộ so khớp định dạng đường dẫn (Ant-style)

    // Danh sách các đường dẫn công khai (Public Paths) không yêu cầu xác thực vai trò
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/**",
            "/images/**",
            "/rooms/**",
            "/api/role-urls/my-permissions",
            "/api/appointments/by-patient/**",
            "/api/medical-records/by-patient/**",
            "/api/health-package-bookings/patient/**",
            "/api/online-consultations/patient/**",
            "/api/room-bookings/by-user/**",
            "/api/prescriptions/patient/**",
            "/api/users",
            "/api/doctors", "/api/doctors/**",
            "/api/services", "/api/services/**",
            "/api/rooms", "/api/rooms/**",
            "/api/specializations", "/api/specializations/**",
            "/api/health-packages", "/api/health-packages/**",
            "/api/hospitals", "/api/hospitals/**");

    // Danh sách đường dẫn chung yêu cầu đăng nhập nhưng không qua ma trận quyền động
    private static final List<String> COMMON_AUTHENTICATED_PATHS = List.of(
            "/api/users/profile"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI(); // Lấy đường dẫn URL yêu cầu (Ví dụ: /api/rooms)
        String httpMethod = request.getMethod(); // Lấy phương thức HTTP (Ví dụ: GET, POST)

        // 1. Kiểm tra nếu URL yêu cầu thuộc danh sách các đường dẫn công khai
        for (String publicPath : PUBLIC_PATHS) {
            if (antPathMatcher.match(publicPath, requestUri)) {
                // Đảm bảo đối với /api/users, chỉ cho phép GET đi qua, các phương thức khác (POST, PUT, DELETE) phải qua phân quyền động
                if ("/api/users".equals(publicPath) && !"GET".equalsIgnoreCase(httpMethod)) {
                    continue;
                }
                // Đối với các dịch vụ công khai khác, cũng chỉ cho phép GET đi qua mà không qua kiểm tra ma trận quyền động
                if ((publicPath.startsWith("/api/doctors")
                        || publicPath.startsWith("/api/services")
                        || publicPath.startsWith("/api/rooms")
                        || publicPath.startsWith("/api/specializations")
                        || publicPath.startsWith("/api/health-packages")
                        || publicPath.startsWith("/api/hospitals"))
                        && !"GET".equalsIgnoreCase(httpMethod)) {
                    continue;
                }
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 2. Bỏ qua các yêu cầu OPTIONS (CORS preflight request gửi từ trình duyệt trước khi gọi API thực tế)
        if ("OPTIONS".equalsIgnoreCase(httpMethod)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Lấy và kiểm tra Header Authorization xem có chứa JWT Token hợp lệ không
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        try {
            // Trích xuất vai trò (Role) của người dùng từ Token JWT
            final String role = jwtConfig.extractRole(jwt);

            if (role == null) {
                sendForbidden(response, "No role found in token");
                return;
            }

            // 4. Cơ chế đặc quyền: 
            // - ADMIN: Tự động cho phép truy cập tất cả tài nguyên quản trị.
            // - USER / PATIENT: Chỉ giới hạn ở các API dành cho khách hàng, bỏ qua kiểm tra ma trận quyền động.
            if ("ADMIN".equalsIgnoreCase(role) || "USER".equalsIgnoreCase(role) || "PATIENT".equalsIgnoreCase(role)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Kiểm tra trạng thái Kích hoạt của Vai trò động
            com.myproject.clinic.entity.Role roleEntity = roleRepository.findByName(role).orElse(null);
            if (roleEntity == null || Boolean.FALSE.equals(roleEntity.getIsActive())) {
                sendForbidden(response, "Vai trò này đã bị khóa hoặc không tồn tại: " + role);
                return;
            }

            // 4.5. Cho phép các đường dẫn chung (như /api/users/profile) đối với mọi tài khoản hợp lệ
            for (String commonPath : COMMON_AUTHENTICATED_PATHS) {
                if (antPathMatcher.match(commonPath, requestUri)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            // 5. Truy vấn danh sách các mẫu URL hợp lệ từ DB cho Vai trò và Phương thức HTTP này
            List<RoleUrl> allowedUrls = roleUrlRepository.findByRoleNameAndHttpMethod(role, httpMethod.toUpperCase());

            boolean isAllowed = false;
            for (RoleUrl roleUrl : allowedUrls) {
                String pattern = roleUrl.getUrlPattern();
                
                // Hỗ trợ so khớp đường dẫn gốc khi pattern kết thúc bằng "/**" (VD: /api/health-packages)
                String basePattern = pattern;
                if (pattern.endsWith("/**")) {
                    basePattern = pattern.substring(0, pattern.length() - 3);
                }
                
                if (antPathMatcher.match(pattern, requestUri)
                        || antPathMatcher.match(basePattern, requestUri)) {
                    isAllowed = true;
                    break;
                }
            }

            // 6. Nếu không tìm thấy quyền hợp lệ trong DB, trả về mã lỗi 403 Forbidden (Bị từ chối truy cập)
            if (!isAllowed) {
                sendForbidden(response, "Access denied for role: " + role);
                return;
            }
        } catch (Exception e) {
            // Nếu giải mã lỗi, chuyển tiếp sang bộ lọc tiếp theo (JwtAuthenticationFilter sẽ chịu trách nhiệm trả về lỗi chưa đăng nhập)
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Hàm phụ trợ gửi phản hồi lỗi 403 Forbidden dạng JSON.
     */
    private void sendForbidden(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{\"status\":403,\"message\":\"" + message + "\"}");
    }
}
