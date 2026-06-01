CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    service_id BIGINT,
    schedule_id BIGINT,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(30),
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES users(id),
    CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    CONSTRAINT fk_appointments_service FOREIGN KEY (service_id) REFERENCES services(id),
    CONSTRAINT fk_appointments_schedule FOREIGN KEY (schedule_id) REFERENCES doctor_schedules(id)
);


