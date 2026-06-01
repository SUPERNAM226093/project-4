CREATE TABLE role_urls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    url_pattern VARCHAR(255) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    description VARCHAR(255),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
