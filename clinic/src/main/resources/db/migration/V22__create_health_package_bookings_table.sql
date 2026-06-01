CREATE TABLE health_package_bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    health_package_id BIGINT NOT NULL,
    booking_date DATE NOT NULL,
    booking_time TIME NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING',
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hpb_patient FOREIGN KEY (patient_id) REFERENCES users(id),
    CONSTRAINT fk_hpb_package FOREIGN KEY (health_package_id) REFERENCES health_packages(id)
);
