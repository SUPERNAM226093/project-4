package com.myproject.clinic.repository;

import com.myproject.clinic.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Giao diện Repository cung cấp các phương thức truy xuất dữ liệu từ Database cho Role.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
