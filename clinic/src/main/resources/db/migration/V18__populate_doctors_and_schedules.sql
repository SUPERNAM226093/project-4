
-- 1. Ensure Specializations exist
-- Syncing with the provided icon list and specific names
INSERT INTO specializations (name, description, feature_image) VALUES 
('Nội Cơ Xương Khớp', 'Chuyên khoa Nội Cơ Xương Khớp chuyên điều trị các bệnh lý về khớp, cột sống và phần mềm quanh khớp.', 'https://img.icons8.com/color/96/knee-joint.png'),
('Tai Mũi Họng', 'Chuyên khoa điều trị các bệnh về Tai, Mũi và Họng cho cả trẻ em và người lớn.', 'https://img.icons8.com/color/96/throat.png'),
('Mắt', 'Chuyên khoa Mắt cung cấp các dịch vụ khám và điều trị bệnh lý nhãn khoa.', 'https://img.icons8.com/color/96/visible.png'),
('Nội Tiêu Hoá', 'Chuyên khoa chẩn đoán và điều trị các bệnh lý liên quan đến hệ tiêu hóa, gan mật và tụy.', 'https://img.icons8.com/color/96/stomach.png'),
('Nội Truyền Nhiễm', 'Chuyên khoa điều trị các bệnh truyền nhiễm và nhiệt đới.', 'https://img.icons8.com/color/96/liver.png'),
('Nội Hô Hấp', 'Chuyên khoa điều trị các bệnh lý về phổi và đường hô hấp.', 'https://img.icons8.com/color/96/lungs.png'),
('Nội Tiết Niệu', 'Chuyên khoa điều trị các bệnh về hệ tiết niệu và nam học.', 'https://img.icons8.com/color/96/kidney.png'),
('Ngoại Cơ Xương Khớp', 'Chuyên khoa phẫu thuật và điều trị ngoại khoa các bệnh lý cơ xương khớp.', 'https://img.icons8.com/color/96/broken-bone.png'),
('Sản - Phụ Khoa', 'Chuyên khoa chăm sóc sức khỏe phụ nữ và thai sản.', 'https://img.icons8.com/color/96/uterus.png')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 2. Create User Accounts for Doctors
-- Password '123456' hashed with BCrypt
SET @pwd = '$2a$10$h.dl5TiVGsOTjheTuEizpOnN3MvSOfWNoP.5YvOn0noX9E6M.p0O.';
SET @doc_role = (SELECT id FROM roles WHERE name = 'DOCTOR' LIMIT 1);

INSERT IGNORE INTO users (email, password_hash, full_name, role_id, status, created_at) VALUES 
('dodangkhoa@gmail.com', @pwd, 'BS.CKI. Đỗ Đăng Khoa', @doc_role, 'ACTIVE', NOW()),
('vuthiha@gmail.com', @pwd, 'BS CKI. Vũ Thị Hà', @doc_role, 'ACTIVE', NOW()),
('nguyenthisen@gmail.com', @pwd, 'BS CKI. Nguyễn Thị Sen', @doc_role, 'ACTIVE', NOW()),
('lehoangthien@gmail.com', @pwd, 'Ths BS. Lê Hoàng Thiên', @doc_role, 'ACTIVE', NOW()),
('dangquocbao@gmail.com', @pwd, 'BS. Đặng Quốc Bảo', @doc_role, 'ACTIVE', NOW()),
('hoangthianhthu@gmail.com', @pwd, 'BS. Hoàng Thị Anh Thư', @doc_role, 'ACTIVE', NOW()),
('nguyenphucthien@gmail.com', @pwd, 'BS CKI. Nguyễn Phúc Thiện', @doc_role, 'ACTIVE', NOW()),
('nguyenthihuong@gmail.com', @pwd, 'Bác sĩ Nguyễn Thị Hường', @doc_role, 'ACTIVE', NOW());

-- 3. Insert into Doctors table
-- Note: Mapping each doctor to a unique specialization
INSERT IGNORE INTO doctors (user_id, specialization_id, experience_years, bio, feature_image)
SELECT u.id, s.id, 10, CONCAT('Bác sĩ chuyên khoa tại MedPro với hơn 10 năm kinh nghiệm trong lĩnh vực ', s.name), 
       CASE 
         WHEN u.full_name LIKE '%Đỗ Đăng Khoa%' THEN 'https://img.freepik.com/free-photo/doctor-presenting-something-from-white-background_1368-587.jpg'
         WHEN u.full_name LIKE '%Vũ Thị Hà%' THEN 'https://img.freepik.com/free-photo/female-doctor-presenting-something_1368-591.jpg'
         WHEN u.full_name LIKE '%Nguyễn Thị Sen%' THEN 'https://img.freepik.com/free-photo/young-female-doctor-isolated-white-background_1303-11663.jpg'
         WHEN u.full_name LIKE '%Lê Hoàng Thiên%' THEN 'https://img.freepik.com/free-photo/portrait-doctor_144627-39390.jpg'
         ELSE 'https://img.freepik.com/free-photo/doctor-with-his-arms-crossed-white-background_1368-579.jpg'
       END
FROM users u
JOIN (SELECT 'dodangkhoa@gmail.com' as email, 'Nội Cơ Xương Khớp' as spec UNION ALL
      SELECT 'vuthiha@gmail.com', 'Tai Mũi Họng' UNION ALL
      SELECT 'nguyenthisen@gmail.com', 'Mắt' UNION ALL
      SELECT 'lehoangthien@gmail.com', 'Nội Tiêu Hoá' UNION ALL
      SELECT 'dangquocbao@gmail.com', 'Nội Truyền Nhiễm' UNION ALL
      SELECT 'hoangthianhthu@gmail.com', 'Nội Hô Hấp' UNION ALL
      SELECT 'nguyenphucthien@gmail.com', 'Nội Tiết Niệu' UNION ALL
      SELECT 'nguyenthihuong@gmail.com', 'Ngoại Cơ Xương Khớp'
     ) mapping ON u.email = mapping.email
JOIN specializations s ON s.name = mapping.spec;

-- 4. Generate Doctor Schedules (07:00 - 17:00, 15 min intervals)
-- Date: 2026-05-01
-- 10 hours * 4 = 40 slots per doctor. (Actually 07:00 to 17:00 is 10 hours, slots are e.g. 16:45-17:00).
INSERT INTO doctor_schedules (doctor_id, work_date, start_time, end_time, created_at)
SELECT 
    d.id,
    '2026-05-01',
    TIME(ADDTIME('07:00:00', SEC_TO_TIME(t.n * 900))),
    TIME(ADDTIME('07:15:00', SEC_TO_TIME(t.n * 900))),
    NOW()
FROM 
    (SELECT d.id FROM doctors d 
     JOIN users u ON d.user_id = u.id 
     WHERE u.email IN ('dodangkhoa@gmail.com', 'vuthiha@gmail.com', 'nguyenthisen@gmail.com', 'lehoangthien@gmail.com', 
                      'dangquocbao@gmail.com', 'hoangthianhthu@gmail.com', 'nguyenphucthien@gmail.com', 'nguyenthihuong@gmail.com')
    ) as d
CROSS JOIN 
    (SELECT n FROM (
        SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL 
        SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL 
        SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL 
        SELECT 15 UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL 
        SELECT 20 UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL 
        SELECT 25 UNION ALL SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL 
        SELECT 30 UNION ALL SELECT 31 UNION ALL SELECT 32 UNION ALL SELECT 33 UNION ALL SELECT 34 UNION ALL 
        SELECT 35 UNION ALL SELECT 36 UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39
    ) nn) t;
