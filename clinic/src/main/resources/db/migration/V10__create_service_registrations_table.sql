CREATE TABLE service_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    status VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_service_registrations_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_service_registrations_service FOREIGN KEY (service_id) REFERENCES services(id)
);
