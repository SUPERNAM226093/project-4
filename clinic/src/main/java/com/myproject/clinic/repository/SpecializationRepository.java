package com.myproject.clinic.repository;

import com.myproject.clinic.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Giao diện Repository cung cấp các phương thức truy xuất dữ liệu từ Database cho Specialization.
 */
@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    java.util.List<Specialization> findByStatus(String status);
}
