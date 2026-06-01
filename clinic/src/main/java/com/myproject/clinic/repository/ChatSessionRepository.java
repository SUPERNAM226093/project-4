package com.myproject.clinic.repository;

import com.myproject.clinic.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, String> {

    Optional<ChatSession> findBySessionIdAndExpiresAtAfter(String sessionId, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM ChatSession c WHERE c.expiresAt < :now")
    int deleteByExpiresAtBefore(@Param("now") LocalDateTime now);
}
