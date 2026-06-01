CREATE TABLE medical_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL UNIQUE,
    doctor_id BIGINT NOT NULL,
    diagnosis TEXT,
    conclusion TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_medical_records_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    CONSTRAINT fk_medical_records_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);
