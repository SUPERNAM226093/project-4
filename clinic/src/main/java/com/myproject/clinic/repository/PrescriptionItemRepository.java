package com.myproject.clinic.repository;

import com.myproject.clinic.entity.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Giao diện Repository cung cấp các phương thức truy xuất dữ liệu từ Database cho PrescriptionItem.
 */
@Repository
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long> {
}
