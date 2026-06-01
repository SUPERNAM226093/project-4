CREATE TABLE services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(12, 2),
    type VARCHAR(50),
    duration_minutes INT,
    created_by BIGINT,
    CONSTRAINT fk_services_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);
