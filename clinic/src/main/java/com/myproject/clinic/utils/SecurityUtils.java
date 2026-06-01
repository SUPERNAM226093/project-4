package com.myproject.clinic.utils;

import com.myproject.clinic.entity.Doctor;
import com.myproject.clinic.entity.User;
import com.myproject.clinic.repository.DoctorRepository;
import com.myproject.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    /** Lấy User đang đăng nhập từ SecurityContext. */
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng không tồn tại"));
    }

    /** Lấy tên Role đang đăng nhập từ SecurityContext (Token JWT). */
    public static String getCurrentUserRole() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        
        // JWT Filter thêm authority "ROLE_..." vào context
        return auth.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> {
                    String authority = grantedAuthority.getAuthority();
                    if (authority.startsWith("ROLE_")) {
                        return authority.substring(5);
                    }
                    return authority;
                })
                .orElse(null);
    }

    /**
     * Kiểm tra user hiện tại có role DOCTOR không.
     * Dùng để phân nhánh logic trong service.
     */
    public boolean isDoctor() {
        User user = getCurrentUser();
        return "DOCTOR".equalsIgnoreCase(user.getRole().getName());
    }

    /**
     * Kiểm tra user hiện tại có role STAFF không.
     */
    public boolean isStaff() {
        User user = getCurrentUser();
        return "STAFF".equalsIgnoreCase(user.getRole().getName());
    }

    /**
     * Kiểm tra user hiện tại có quyền truy cập toàn cục (global data access) hay không.
     * Áp dụng cho ADMIN, STAFF và tất cả các CUSTOM ROLES (Dược sĩ, Kế toán...).
     * DOCTOR bị giới hạn dữ liệu cá nhân. PATIENT/USER không có quyền quản trị.
     */
    public boolean hasGlobalDataAccess() {
        String roleName = getCurrentUser().getRole().getName();
        return !"DOCTOR".equalsIgnoreCase(roleName) 
            && !"PATIENT".equalsIgnoreCase(roleName) 
            && !"USER".equalsIgnoreCase(roleName);
    }

    /**
     * Lấy Doctor entity của người dùng đang đăng nhập.
     * Nếu role là DOCTOR nhưng không tìm thấy Doctor entity → 403 FORBIDDEN.
     * Nếu role không phải DOCTOR → trả null (để gọi nơi nào cần phân luồng).
     */
    public Doctor getCurrentDoctorOrNull() {
        User user = getCurrentUser();
        if (!"DOCTOR".equalsIgnoreCase(user.getRole().getName())) {
            return null;
        }
        return doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("[SECURITY_AUDIT] User {} has DOCTOR role but no Doctor profile found.", user.getId());
                    return new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "Hồ sơ bác sĩ chưa được khởi tạo. Vui lòng liên hệ quản trị viên.");
                });
    }

    /**
     * Bắt buộc trả về Doctor entity — dùng trong context chắc chắn là DOCTOR.
     * Throw 403 nếu không có Doctor entity.
     */
    public Doctor requireCurrentDoctor() {
        Doctor doctor = getCurrentDoctorOrNull();
        if (doctor == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ Bác sĩ mới có quyền thực hiện thao tác này.");
        }
        return doctor;
    }

    /**
     * Validate quyền sở hữu: nếu là DOCTOR, doctorId của record phải khớp với doctorId hiện tại.
     * Ghi audit log nếu bị chặn.
     *
     * @param resourceType tên loại tài nguyên (dùng cho log)
     * @param resourceId   id của tài nguyên (dùng cho log)
     * @param ownerDoctorId doctorId của record đang xét
     */
    public void assertDoctorOwnership(String resourceType, Long resourceId, Long ownerDoctorId) {
        Doctor currentDoctor = getCurrentDoctorOrNull();
        if (currentDoctor == null) return; // STAFF/ADMIN: bỏ qua kiểm tra

        if (!currentDoctor.getId().equals(ownerDoctorId)) {
            User user = getCurrentUser();
            log.warn("[SECURITY_AUDIT] UNAUTHORIZED_ACCESS | userId={} role=DOCTOR tried to access {} id={} owned by doctorId={}",
                    user.getId(), resourceType, resourceId, ownerDoctorId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Bạn không có quyền truy cập tài nguyên này.");
        }
    }

    /**
     * Phát hiện spoofing: bác sĩ truyền doctorId của người khác trong request body.
     * Ghi log suspicious và trả về doctorId thực của phiên đăng nhập hiện tại.
     *
     * @param requestedDoctorId doctorId từ request body
     * @return doctorId hợp lệ (luôn là doctorId của phiên hiện tại)
     */
    public Long resolveAndValidateDoctorId(Long requestedDoctorId) {
        Doctor currentDoctor = getCurrentDoctorOrNull();
        if (currentDoctor == null) {
            // STAFF/ADMIN: giữ nguyên giá trị từ request
            return requestedDoctorId;
        }
        if (requestedDoctorId != null && !requestedDoctorId.equals(currentDoctor.getId())) {
            User user = getCurrentUser();
            log.warn("[SECURITY_AUDIT] SUSPICIOUS_ATTEMPT | userId={} tried to spoof doctorId={} (own doctorId={})",
                    user.getId(), requestedDoctorId, currentDoctor.getId());
        }
        return currentDoctor.getId();
    }
}
