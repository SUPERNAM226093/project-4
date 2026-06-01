CREATE TABLE lab_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medical_record_id BIGINT NOT NULL,
    ordered_by BIGINT NOT NULL,
    status VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lab_orders_medical_record FOREIGN KEY (medical_record_id) REFERENCES medical_records(id),
    CONSTRAINT fk_lab_orders_ordered_by FOREIGN KEY (ordered_by) REFERENCES doctors(id)
);

CREATE TABLE lab_order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lab_order_id BIGINT NOT NULL,
    service_id BIGINT,
    result TEXT,
    file_url VARCHAR(500),
    status VARCHAR(30),
    CONSTRAINT fk_lab_order_items_order FOREIGN KEY (lab_order_id) REFERENCES lab_orders(id),
    CONSTRAINT fk_lab_order_items_service FOREIGN KEY (service_id) REFERENCES services(id)
);
