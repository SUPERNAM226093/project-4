package com.myproject.clinic.repository;

import com.myproject.clinic.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Giao diện Repository cung cấp các phương thức truy xuất dữ liệu từ Database cho Room.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByIsActiveTrue();
    boolean existsByRoomCode(String roomCode);
}
