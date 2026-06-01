package com.myproject.clinic.repository;

import com.myproject.clinic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Giao diện Repository cung cấp các phương thức truy xuất dữ liệu từ Database cho User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    @org.springframework.data.jpa.repository.Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.role.id = :roleId")
    boolean existsByRoleId(@org.springframework.data.repository.query.Param("roleId") Long roleId);

    java.util.List<User> findByRoleId(Long roleId);

    long countByRoleNameAndCreatedAtBetween(String roleName, java.time.LocalDateTime start, java.time.LocalDateTime end);

    long countByRoleNameIgnoreCaseAndCreatedAtBetween(String roleName, java.time.LocalDateTime start, java.time.LocalDateTime end);

    java.util.List<User> findByRoleNameAndCreatedAtBetween(String roleName, java.time.LocalDateTime start, java.time.LocalDateTime end);

    java.util.List<User> findByRoleNameIgnoreCaseAndCreatedAtBetween(String roleName, java.time.LocalDateTime start, java.time.LocalDateTime end);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query("UPDATE User u SET u.createdAt = :now WHERE u.createdAt IS NULL")
    void fixNullDates(java.time.LocalDateTime now);
}
