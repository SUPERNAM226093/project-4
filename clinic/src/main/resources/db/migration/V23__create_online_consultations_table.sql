CREATE TABLE online_consultations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    specialization_id BIGINT,
    service_id BIGINT,
    phone_number VARCHAR(15) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING / PAID / CANCELLED',
    meeting_link VARCHAR(500) NULL COMMENT 'Filled by staff after payment confirmed',
    expired_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_oc_patient FOREIGN KEY (patient_id) REFERENCES users(id),
    CONSTRAINT fk_oc_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    CONSTRAINT fk_oc_specialization FOREIGN KEY (specialization_id) REFERENCES specializations(id) ON DELETE SET NULL,
    CONSTRAINT fk_oc_service FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE SET NULL
);
