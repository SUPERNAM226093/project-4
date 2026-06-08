-- ============================================================
-- V30: Chuyển role_id thành role_name và xóa bảng roles
-- ============================================================

-- 1. Thêm cột role_name vào users
ALTER TABLE users ADD COLUMN role_name VARCHAR(50);

-- 2. Cập nhật dữ liệu từ bảng roles sang users
UPDATE users u
JOIN roles r ON u.role_id = r.id
SET u.role_name = r.name;

-- 3. Xóa khóa ngoại và cột role_id
ALTER TABLE users DROP FOREIGN KEY fk_users_role;
ALTER TABLE users DROP COLUMN role_id;

-- 4. Xóa bảng roles
DROP TABLE IF EXISTS roles;
