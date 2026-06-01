ALTER TABLE role_urls
    ADD COLUMN permission_source VARCHAR(16) NOT NULL DEFAULT 'MANUAL',
    ADD COLUMN matrix_module VARCHAR(64) NULL;

CREATE INDEX idx_role_urls_matrix ON role_urls (role_id, matrix_module, permission_source);
