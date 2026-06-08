package com.myproject.clinic.config;

import com.myproject.clinic.entity.PermissionSource;
import com.myproject.clinic.entity.Role;
import com.myproject.clinic.entity.RoleUrl;
import com.myproject.clinic.permissionmatrix.PermissionModuleCatalog;
import com.myproject.clinic.repository.RoleRepository;
import com.myproject.clinic.repository.RoleUrlRepository;
import com.myproject.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DataInitializer — Chạy tự động 1 lần khi ứng dụng khởi động.
 *
 * Nhiệm vụ: Nạp quyền mặc định vào DB cho STAFF và DOCTOR theo ma trận chuẩn.
 *
 * ADMIN: Không seed vào DB. Admin được bypass hoàn toàn trong RoleUrlAuthorizationFilter
 * (dòng 76-78 trong filter), nên thêm tính năng mới cũng tự có quyền.
 *
 * PATIENT: Không thuộc trang quản lý admin, không seed.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleUrlRepository roleUrlRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    /**
     * Ma trận quyền mặc định cho STAFF.
     * Mỗi phần tử: { moduleKey, action }
     * action: "view" | "create" | "edit" | "delete"
     * edit   → sẽ tạo ra cả PUT và PATCH
     */
    private static final List<String[]> STAFF_PERMISSIONS = List.of(
            // Dịch vụ & Phòng: Xem, Thêm, Sửa
            new String[]{"rooms", "view"},
            new String[]{"rooms", "create"},
            new String[]{"rooms", "edit"},
            // Đăng ký dịch vụ: Xem, Thêm, Sửa
            new String[]{"room-bookings", "view"},
            new String[]{"room-bookings", "create"},
            new String[]{"room-bookings", "edit"},
            // Gói khám sức khỏe: Xem, Thêm, Sửa
            new String[]{"health-packages", "view"},
            new String[]{"health-packages", "create"},
            new String[]{"health-packages", "edit"},
            // Đăng ký gói khám: Xem, Thêm, Sửa
            new String[]{"health-package-bookings", "view"},
            new String[]{"health-package-bookings", "create"},
            new String[]{"health-package-bookings", "edit"},
            // Tư vấn trực tuyến: Xem, Sửa
            new String[]{"online-consultations", "view"},
            new String[]{"online-consultations", "edit"},
            // Lịch hẹn: Xem, Sửa
            new String[]{"appointments", "view"},
            new String[]{"appointments", "edit"}
    );

    /**
     * Ma trận quyền mặc định cho DOCTOR.
     * Bác sĩ chỉ tập trung vào lâm sàng.
     */
    private static final List<String[]> DOCTOR_PERMISSIONS = List.of(
            // Lịch hẹn: xem + sửa (appointments)
            new String[]{"appointments", "view"},
            new String[]{"appointments", "edit"},
            // Tư vấn trực tuyến: xem + sửa (online-consultations)
            new String[]{"online-consultations", "view"},
            new String[]{"online-consultations", "edit"},
            // Hồ sơ bệnh án: xem + sửa (medical-records)
            new String[]{"medical-records", "view"},
            new String[]{"medical-records", "edit"},
            // Đơn thuốc: xem + sửa (prescriptions)
            new String[]{"prescriptions", "view"},
            new String[]{"prescriptions", "edit"}
    );

    @Override
    public void run(String... args) {
        System.out.println(">>> [DataInitializer] Đang kiểm tra và nạp quyền mặc định...");

        // Cập nhật mật khẩu admin@gmail.com thành '123456'
        userRepository.findByEmail("admin@gmail.com").ifPresent(admin -> {
            admin.setPasswordHash(passwordEncoder.encode("123456"));
            userRepository.save(admin);
            System.out.println(">>> [DataInitializer] Updated password hash for admin@gmail.com to match '123456'");
        });

        // Xóa toàn bộ quyền MATRIX cũ của STAFF và DOCTOR để seed lại sạch
        // (tránh bị nhân đôi nếu khởi động lại nhiều lần)
        cleanRolePermissions("STAFF");
        cleanRolePermissions("DOCTOR");

        // Seed quyền mặc định cho STAFF
        seedRolePermissions("STAFF", STAFF_PERMISSIONS);

        // Seed quyền mặc định cho DOCTOR
        seedRolePermissions("DOCTOR", DOCTOR_PERMISSIONS);

        System.out.println(">>> [DataInitializer] Hoàn tất nạp quyền.");
    }

    /**
     * Xóa toàn bộ quyền MATRIX hiện có của một role trong DB.
     * Mục đích: tránh duplicate khi DataInitializer chạy lại nhiều lần.
     *
     * @param roleName Tên role (STAFF / DOCTOR)
     */
    private void cleanRolePermissions(String roleName) {
        roleRepository.findByName(roleName).ifPresent(role -> {
            // Xóa tất cả quyền nguồn MATRIX của role này
            List<RoleUrl> existing = roleUrlRepository.findByRoleIdAndPermissionSource(
                    role.getId(), PermissionSource.MATRIX);
            roleUrlRepository.deleteAll(existing);
            System.out.println(">>> [DataInitializer] Đã xóa " + existing.size() + " quyền cũ của role: " + roleName);
        });
    }

    /**
     * Seed quyền mặc định cho một role theo danh sách { moduleKey, action }.
     * Tra cứu URL pattern từ PermissionModuleCatalog (nguồn duy nhất).
     * Mỗi action được chuyển thành HTTP method(s) nhờ httpMethodsForAction().
     *
     * @param roleName    Tên role (STAFF / DOCTOR)
     * @param permissions Danh sách cặp [moduleKey, action]
     */
    private void seedRolePermissions(String roleName, List<String[]> permissions) {
        // Tìm role trong DB — nếu không tồn tại thì bỏ qua
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) {
            System.out.println(">>> [DataInitializer] Không tìm thấy role: " + roleName + " — bỏ qua.");
            return;
        }

        int count = 0;
        for (String[] perm : permissions) {
            String moduleKey = perm[0]; // Ví dụ: "appointments"
            String action    = perm[1]; // Ví dụ: "edit"

            // Lấy định nghĩa module từ Catalog (URL pattern, label...)
            PermissionModuleCatalog.findModule(moduleKey).ifPresentOrElse(def -> {
                // Mỗi action có thể map sang nhiều HTTP method (edit → PUT + PATCH)
                for (String method : PermissionModuleCatalog.httpMethodsForAction(action)) {
                    for (String pattern : def.path().split(",")) {
                        RoleUrl roleUrl = RoleUrl.builder()
                                .role(role)
                                .urlPattern(pattern.trim())       // Ví dụ: /appointments/**
                                .httpMethod(method)            // Ví dụ: PUT
                                .description("Mặc định: " + def.label())
                                .permissionSource(PermissionSource.MATRIX) // Nguồn = MATRIX (không phải MANUAL)
                                .matrixModule(moduleKey)       // Liên kết ngược về module key để FE hiển thị đúng
                                .build();
                        roleUrlRepository.save(roleUrl);
                    }
                }
            }, () -> System.out.println(">>> [DataInitializer] Module không tồn tại trong catalog: " + moduleKey));

            count++;
        }

        System.out.println(">>> [DataInitializer] Đã seed " + permissions.size() + " quyền cho role: " + roleName);
    }
}
