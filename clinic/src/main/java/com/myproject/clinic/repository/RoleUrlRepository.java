package com.myproject.clinic.repository;

import com.myproject.clinic.entity.PermissionSource;
import com.myproject.clinic.entity.RoleUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Giao diện Repository cung cấp các phương thức truy xuất dữ liệu từ Database cho RoleUrl.
 */
@Repository
public interface RoleUrlRepository extends JpaRepository<RoleUrl, Long> {
    List<RoleUrl> findByRoleId(Long roleId);

    List<RoleUrl> findByRoleName(String roleName);

    List<RoleUrl> findByRoleNameAndHttpMethod(String roleName, String httpMethod);

    List<RoleUrl> findByRoleIdAndPermissionSource(Long roleId, PermissionSource permissionSource);

    List<RoleUrl> findByRoleIdAndPermissionSourceAndMatrixModuleIn(
            Long roleId, PermissionSource permissionSource, Collection<String> matrixModules);

    @Modifying
    @Query("DELETE FROM RoleUrl r WHERE r.role.id = :roleId AND r.permissionSource = :source AND r.matrixModule IN :modules")
    void deleteMatrixModules(
            @Param("roleId") Long roleId,
            @Param("source") PermissionSource source,
            @Param("modules") Collection<String> modules);
}
