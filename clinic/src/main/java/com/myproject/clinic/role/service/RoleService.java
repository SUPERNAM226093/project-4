package com.myproject.clinic.role.service;

import com.myproject.clinic.entity.Role;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.RoleRepository;
import com.myproject.clinic.role.dto.RoleRequest;
import com.myproject.clinic.role.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final com.myproject.clinic.repository.UserRepository userRepository;
    private final com.myproject.clinic.repository.RoleUrlRepository roleUrlRepository;

    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RoleResponse findById(Long id) {
        return toResponse(roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id)));
    }

    public RoleResponse create(RoleRequest request) {
        Role role = Role.builder()
                .name(request.getName())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        return toResponse(roleRepository.save(role));
    }

    public RoleResponse update(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        role.setName(request.getName());
        if (request.getIsActive() != null) {
            role.setIsActive(request.getIsActive());
        }
        return toResponse(roleRepository.save(role));
    }

    @org.springframework.transaction.annotation.Transactional
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", id);
        }

        // Thay vì báo lỗi, ta luân chuyển người dùng sang Role mặc định
        if (userRepository.existsByRoleId(id)) {
            Role defaultRole = roleRepository.findByName("PATIENT")
                    .orElseGet(() -> roleRepository.findByName("USER")
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ mặc định (PATIENT/USER) để luân chuyển người dùng!")));

            java.util.List<com.myproject.clinic.entity.User> usersToUpdate = userRepository.findByRoleId(id);
            for (com.myproject.clinic.entity.User u : usersToUpdate) {
                u.setRole(defaultRole);
            }
            userRepository.saveAll(usersToUpdate);
        }

        // Lấy danh sách RoleUrl theo roleId
        List<com.myproject.clinic.entity.RoleUrl> roleUrls = roleUrlRepository.findByRoleId(id);
        if (!roleUrls.isEmpty()) {
            roleUrlRepository.deleteAllInBatch(roleUrls);
        }

        roleRepository.deleteById(id);
    }

    private RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .isActive(role.getIsActive())
                .build();
    }
}
