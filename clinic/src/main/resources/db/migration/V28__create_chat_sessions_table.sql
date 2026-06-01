CREATE TABLE chat_sessions (
    session_id  VARCHAR(64)  NOT NULL,
    user_id     BIGINT       NULL,
    state_json  JSON         NOT NULL,
    expires_at  DATETIME(3)  NOT NULL,
    updated_at  DATETIME(3)  NOT NULL,
    PRIMARY KEY (session_id),
    INDEX idx_chat_sessions_expires (expires_at),
    INDEX idx_chat_sessions_user (user_id)
);
