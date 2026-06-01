package com.myproject.clinic.permissionmatrix.service;

import com.myproject.clinic.entity.PermissionSource;
import com.myproject.clinic.entity.Role;
import com.myproject.clinic.entity.RoleUrl;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.permissionmatrix.PermissionModuleCatalog;
import com.myproject.clinic.permissionmatrix.dto.*;
import com.myproject.clinic.repository.RoleRepository;
import com.myproject.clinic.repository.RoleUrlRepository;
import com.myproject.clinic.roleurl.dto.RoleUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PermissionMatrixService {

    private final RoleRepository roleRepository;
    private final RoleUrlRepository roleUrlRepository;

    /** Trả về danh sách toàn bộ module có trong catalog để FE render bảng ma trận. */
    public PermissionMatrixCatalogResponse getCatalog() {
        return PermissionMatrixCatalogResponse.builder()
                .groups(PermissionModuleCatalog.groups())
                .build();
    }

    /** Lấy trạng thái phân quyền hiện tại của một role theo roleId. */
    public PermissionMatrixResponse getMatrix(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", roleId));
        return buildResponse(role);
    }

    /**
     * Lưu phân quyền ma trận cho role.
     * Cơ chế: xóa toàn bộ quyền MATRIX cũ của role đó trước,
     * sau đó insert lại theo các module được tick trong request.
     */
    @Transactional
    public PermissionMatrixResponse putMatrix(Long roleId, PermissionMatrixPutRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", roleId));

        // Lấy danh sách module từ request (nếu null thì coi như không tick gì)
        Map<String, ModuleActionsDto> modules = request.getModules() != null
                ? request.getModules()
                : Map.of();

        // Kiểm tra tất cả module key có hợp lệ không
        for (String key : modules.keySet()) {
            if (PermissionModuleCatalog.findModule(key).isEmpty()) {
                throw new IllegalArgumentException("Module key không hợp lệ: " + key);
            }
        }

        // Xóa tất cả quyền MATRIX cũ của các module được gửi lên
        if (!modules.isEmpty()) {
            roleUrlRepository.deleteMatrixModules(roleId, PermissionSource.MATRIX, modules.keySet());
        }

        // Insert lại từng quyền được tick
        for (Map.Entry<String, ModuleActionsDto> entry : modules.entrySet()) {
            String moduleKey = entry.getKey();
            ModuleActionsDto actions = entry.getValue();
            PermissionModuleCatalog.ModuleDefinition def = PermissionModuleCatalog.findModule(moduleKey).orElseThrow();

            // Duyệt qua 4 loại hành động: view, create, edit, delete
            for (String action : PermissionModuleCatalog.allActions()) {
                if (!isActionEnabled(actions, action)) continue;

                // Mỗi hành động có thể map sang nhiều HTTP method (ví dụ edit → PUT + PATCH)
                for (String method : PermissionModuleCatalog.httpMethodsForAction(action)) {
                    for (String pattern : def.path().split(",")) {
                        RoleUrl row = RoleUrl.builder()
                                .role(role)
                                .urlPattern(pattern.trim())
                                .httpMethod(method)
                                .description("Matrix: " + def.label())
                                .permissionSource(PermissionSource.MATRIX)
                                .matrixModule(moduleKey)
                                .build();
                        roleUrlRepository.save(row);
                    }
                }
            }
        }

        return buildResponse(role);
    }

    /**
     * Xây dựng response trả về FE: bao gồm danh sách module và trạng thái tích hiện tại.
     * Không còn cảnh báo MANUAL vì đã xóa toàn bộ kiểu phân quyền thủ công.
     */
    private PermissionMatrixResponse buildResponse(Role role) {
        Long roleId = role.getId();

        // Lấy toàn bộ quyền MATRIX của role này từ DB
        List<RoleUrl> matrixRows = roleUrlRepository.findByRoleIdAndPermissionSource(roleId, PermissionSource.MATRIX);

        // Khởi tạo map module với tất cả module = false (chưa tick)
        Map<String, ModuleActionsDto> modules = new LinkedHashMap<>();
        for (String key : PermissionModuleCatalog.allModuleKeys()) {
            modules.put(key, ModuleActionsDto.builder().build());
        }

        // Nhóm các quyền đang có theo module key
        Map<String, List<RoleUrl>> byModule = new HashMap<>();
        for (RoleUrl row : matrixRows) {
            if (row.getMatrixModule() != null) {
                byModule.computeIfAbsent(row.getMatrixModule(), k -> new ArrayList<>()).add(row);
            }
        }

        // Cập nhật trạng thái tick từ DB vào map
        for (Map.Entry<String, List<RoleUrl>> e : byModule.entrySet()) {
            modules.put(e.getKey(), deriveActions(e.getValue()));
        }

        return PermissionMatrixResponse.builder()
                .roleId(roleId)
                .roleName(role.getName())
                .modules(modules)
                // Không còn manualPermissions và warnings vì đã xóa kiểu phân quyền thủ công
                .manualPermissions(List.of())
                .warnings(List.of())
                .build();
    }

    /**
     * Từ danh sách RoleUrl của 1 module, suy ra các hành động nào đang được tick.
     * GET → view, POST → create, PUT/PATCH → edit, DELETE → delete
     */
    private ModuleActionsDto deriveActions(List<RoleUrl> rows) {
        Set<String> methods = new HashSet<>();
        for (RoleUrl row : rows) {
            methods.add(row.getHttpMethod().toUpperCase());
        }
        return ModuleActionsDto.builder()
                .view(methods.contains("GET"))
                .create(methods.contains("POST"))
                .edit(methods.contains("PUT") || methods.contains("PATCH"))
                .delete(methods.contains("DELETE"))
                .build();
    }

    /** Kiểm tra xem một hành động có được bật trong DTO không. */
    private boolean isActionEnabled(ModuleActionsDto actions, String action) {
        return switch (action) {
            case "view"   -> actions.isView();
            case "create" -> actions.isCreate();
            case "edit"   -> actions.isEdit();
            case "delete" -> actions.isDelete();
            default -> false;
        };
    }

    private RoleUrlResponse toRoleUrlResponse(RoleUrl roleUrl) {
        return RoleUrlResponse.builder()
                .id(roleUrl.getId())
                .roleId(roleUrl.getRole().getId())
                .roleName(roleUrl.getRole().getName())
                .urlPattern(roleUrl.getUrlPattern())
                .httpMethod(roleUrl.getHttpMethod())
                .description(roleUrl.getDescription())
                .permissionSource(roleUrl.getPermissionSource().name())
                .matrixModule(roleUrl.getMatrixModule())
                .build();
    }
}
