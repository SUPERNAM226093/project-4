package com.myproject.clinic.roleurl.service;

import com.myproject.clinic.entity.PermissionSource;
import com.myproject.clinic.entity.RoleUrl;
import com.myproject.clinic.repository.RoleUrlRepository;
import com.myproject.clinic.roleurl.dto.RoleUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RoleUrlService — Service xử lý logic phân quyền URL.
 *
 * Sau khi đơn giản hóa:
 * - Chỉ còn 1 method: findByRoleName()
 * - Phục vụ Sidebar FE kiểm tra menu nào được hiển thị.
 * - Các method create/update/delete đã bị xóa (không còn phân quyền thủ công).
 */
@Service
@RequiredArgsConstructor
public class RoleUrlService {

    private final RoleUrlRepository roleUrlRepository;
    private final com.myproject.clinic.repository.RoleRepository roleRepository;

    /**
     * Lấy danh sách tất cả quyền của một role theo tên role.
     * Response format giữ nguyên để Sidebar không cần thay đổi code.
     *
     * @param roleName Tên role (STAFF / DOCTOR)
     * @return Danh sách RoleUrlResponse chứa urlPattern và httpMethod
     */
    public List<RoleUrlResponse> findByRoleName(String roleName) {
        if (!"ADMIN".equalsIgnoreCase(roleName)) {
            com.myproject.clinic.entity.Role role = roleRepository.findByName(roleName).orElse(null);
            if (role == null || Boolean.FALSE.equals(role.getIsActive())) {
                return java.util.Collections.emptyList();
            }
        }
        
        return roleUrlRepository.findByRoleName(roleName)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Chuyển entity RoleUrl sang DTO để trả về FE.
     * permissionSource mặc định là MATRIX nếu null (bảo vệ dữ liệu cũ).
     */
    private RoleUrlResponse toResponse(RoleUrl roleUrl) {
        return RoleUrlResponse.builder()
                .id(roleUrl.getId())
                .roleId(roleUrl.getRole().getId())
                .roleName(roleUrl.getRole().getName())
                .urlPattern(roleUrl.getUrlPattern())
                .httpMethod(roleUrl.getHttpMethod())
                .description(roleUrl.getDescription())
                .permissionSource(roleUrl.getPermissionSource() != null
                        ? roleUrl.getPermissionSource().name()
                        : PermissionSource.MATRIX.name())
                .matrixModule(roleUrl.getMatrixModule())
                .build();
    }
}
