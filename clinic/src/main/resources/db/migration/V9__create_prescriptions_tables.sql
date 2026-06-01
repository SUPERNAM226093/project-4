CREATE TABLE prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medical_record_id BIGINT NOT NULL UNIQUE,
    doctor_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prescriptions_medical_record FOREIGN KEY (medical_record_id) REFERENCES medical_records(id),
    CONSTRAINT fk_prescriptions_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

CREATE TABLE prescription_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    medicine_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    duration VARCHAR(100),
    note TEXT,
    CONSTRAINT fk_prescription_items_prescription FOREIGN KEY (prescription_id) REFERENCES prescriptions(id)
);
