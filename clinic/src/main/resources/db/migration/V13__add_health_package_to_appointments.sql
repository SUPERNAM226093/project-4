ALTER TABLE appointments ADD COLUMN health_package_id BIGINT;
ALTER TABLE appointments ADD CONSTRAINT fk_appointments_health_package FOREIGN KEY (health_package_id) REFERENCES health_packages(id);
