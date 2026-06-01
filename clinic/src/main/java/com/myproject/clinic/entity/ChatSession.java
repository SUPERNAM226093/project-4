package com.myproject.clinic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {

    @Id
    @Column(name = "session_id", length = 64, nullable = false)
    private String sessionId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "state_json", nullable = false, columnDefinition = "json")
    private String stateJson;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
