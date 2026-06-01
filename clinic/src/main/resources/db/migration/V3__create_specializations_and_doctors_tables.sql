CREATE TABLE specializations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    specialization_id BIGINT,
    license_number VARCHAR(100),
    experience_years INT,
    bio TEXT,
    CONSTRAINT fk_doctors_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_doctors_specialization FOREIGN KEY (specialization_id) REFERENCES specializations(id)
);
