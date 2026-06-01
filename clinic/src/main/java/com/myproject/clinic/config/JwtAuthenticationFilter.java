package com.myproject.clinic.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Bộ lọc bảo mật JWT (JwtAuthenticationFilter).
 * Kế thừa OncePerRequestFilter để đảm bảo bộ lọc này chỉ chạy duy nhất 1 lần cho mỗi HTTP Request gửi tới Server.
 * Nhiệm vụ chính: Đón bắt token JWT gửi kèm trong request header, kiểm tra tính hợp lệ và xác thực người dùng.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig; // Lớp cấu hình/tiện ích xử lý JWT (sinh token, giải mã token)
    private final UserDetailsService userDetailsService; // Service để tìm kiếm tài khoản người dùng từ DB

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // 1. Lấy thông tin tiêu đề "Authorization" từ HTTP Request gửi lên
        final String authHeader = request.getHeader("Authorization");

        // Nếu header trống hoặc không bắt đầu bằng tiền tố "Bearer ", bỏ qua bộ lọc này và đi tiếp tới filter tiếp theo
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Cắt bỏ tiền tố "Bearer " (7 ký tự đầu) để lấy chuỗi Token JWT thô
        final String jwt = authHeader.substring(7);
        try {
            // 3. Giải mã token để lấy địa chỉ Email của người dùng
            final String userEmail = jwtConfig.extractUsername(jwt);

            // 4. Nếu email hợp lệ và người dùng chưa được thiết lập trạng thái đăng nhập (xác thực) trong hệ thống
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Truy vấn thông tin tài khoản người dùng từ cơ sở dữ liệu dựa trên Email
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // 5. Kiểm tra thời hạn của token và so khớp thông tin email trong token với UserDetails
                if (jwtConfig.isTokenValid(jwt, userDetails)) {
                    // Tạo đối tượng chứa thông tin xác thực bao gồm quyền hạn (Role/Authorities)
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    // Gắn thêm chi tiết của HTTP request (như IP, Session ID) vào token xác thực
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Thiết lập thông tin người dùng vào ngữ cảnh bảo mật SecurityContext để đánh dấu người dùng đã đăng nhập thành công
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Nếu token bị lỗi (hết hạn, sai chữ ký, format lỗi) → bỏ qua ngoại lệ và không thiết lập đăng nhập
        }

        // 6. Chuyển tiếp Request và Response sang các bộ lọc (filter) tiếp theo trong chuỗi bảo mật
        filterChain.doFilter(request, response);
    }
}
