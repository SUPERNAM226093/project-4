package com.myproject.clinic.permissionmatrix;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class PermissionModuleCatalog {

    private PermissionModuleCatalog() {
    }

    public record ModuleDefinition(String key, String label, String path) {
    }

    public record ModuleGroup(String id, String label, List<ModuleDefinition> modules) {
    }

    private static final List<ModuleGroup> GROUPS = List.of(
            new ModuleGroup("pages", "Trang", List.of(
                    new ModuleDefinition("users", "Người dùng", "/api/users/**"),
                    new ModuleDefinition("roles", "Vai trò & Quyền", "/api/roles/**"),
                    new ModuleDefinition("doctors", "Bác sĩ", "/api/doctors/**"),
                    new ModuleDefinition("specializations", "Chuyên khoa", "/api/specializations/**"),
                    new ModuleDefinition("rooms", "Dịch vụ & Phòng", "/api/rooms/**,/api/services/**"),
                    new ModuleDefinition("room-bookings", "Đăng ký dịch vụ", "/api/room-bookings/**,/api/service-registrations/**"),
                    new ModuleDefinition("health-packages", "Gói khám sức khỏe", "/api/health-packages/**"),
                    new ModuleDefinition("health-package-bookings", "Đăng ký gói khám", "/api/health-package-bookings/**"),
                    new ModuleDefinition("online-consultations", "Tư vấn trực tuyến", "/api/online-consultations/**"),
                    new ModuleDefinition("appointments", "Lịch hẹn", "/api/appointments/**"),
                    new ModuleDefinition("medical-records", "Hồ sơ bệnh án", "/api/medical-records/**"),
                    new ModuleDefinition("prescriptions", "Đơn thuốc", "/api/prescriptions/**"),
                    new ModuleDefinition("role-urls", "Đường dẫn Role", "/api/role-urls/**"))));

    public static List<ModuleGroup> groups() {
        return GROUPS;
    }

    public static Optional<ModuleDefinition> findModule(String key) {
        return GROUPS.stream()
                .flatMap(g -> g.modules().stream())
                .filter(m -> m.key().equals(key))
                .findFirst();
    }

    public static Map<String, ModuleDefinition> allModulesByKey() {
        return GROUPS.stream()
                .flatMap(g -> g.modules().stream())
                .collect(Collectors.toMap(ModuleDefinition::key, m -> m));
    }

    public static List<String> allModuleKeys() {
        return allModulesByKey().keySet().stream().sorted().toList();
    }

    /**
     * Trả về danh sách HTTP method cho từng loại hành động.
     * view   → GET
     * create → POST
     * edit   → PUT, PATCH
     * delete → DELETE
     */
    public static List<String> httpMethodsForAction(String action) {
        return switch (action) {
            case "view"   -> List.of("GET");
            case "create" -> List.of("POST");
            case "edit"   -> List.of("PUT", "PATCH");
            case "delete" -> List.of("DELETE");
            default       -> List.of();
        };
    }

    public static List<String> allActions() {
        return Arrays.asList("view", "create", "edit", "delete");
    }
}
