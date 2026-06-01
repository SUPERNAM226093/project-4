CREATE TABLE health_packages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(12, 2),
    feature_image VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE health_package_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    health_package_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hp_schedules_package FOREIGN KEY (health_package_id) REFERENCES health_packages(id)
);
