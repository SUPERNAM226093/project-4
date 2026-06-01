
-- Insert 10 Health Packages
INSERT INTO health_packages (name, description, price, feature_image, created_at) VALUES
('Gói khám Tổng quát Cơ bản', 'Khám sức khỏe tổng quát định kỳ cho cá nhân, bao gồm các xét nghiệm cơ bản, siêu âm và tư vấn bác sĩ.', 900000.00, 'https://img.freepik.com/free-photo/doctor-presenting-something-from-white-background_1368-587.jpg', NOW()),
('Gói khám Tổng quát Nâng cao', 'Gói khám toàn diện bao gồm đầy đủ các xét nghiệm, chẩn đoán hình ảnh chuyên sâu và đánh giá chức năng các cơ quan.', 1800000.00, 'https://img.freepik.com/free-photo/doctor-consultant-talking-patient-about-medical-results_1150-14923.jpg', NOW()),
('Gói tầm soát Ung thư Phụ nữ', 'Tầm soát chuyên sâu các bệnh lý ung thư phổ biến ở nữ giới như ung thư vú, cổ tử cung, buồng trứng.', 2500000.00, 'https://img.freepik.com/free-photo/female-doctor-doctor-patient-working-hospital_1150-13612.jpg', NOW()),
('Gói tầm soát Ung thư Nam giới', 'Tầm soát các bệnh lý ung thư thường gặp ở nam giới như ung thư gan, phổi, đại trực tràng, tuyến tiền liệt.', 2200000.00, 'https://img.freepik.com/free-photo/man-doctor-discussing-patient_23-2148168453.jpg', NOW()),
('Gói khám Sức khỏe Tiền hôn nhân', 'Kiểm tra sức khỏe tổng quát và sức khỏe sinh sản cho các cặp đôi trước khi kết hôn.', 1500000.00, 'https://img.freepik.com/free-photo/happy-couple-doctor-appointment-clinic_23-2148901764.jpg', NOW()),
('Gói tầm soát Tim mạch chuyên sâu', 'Đánh giá nguy cơ tim mạch, bao gồm điện tâm đồ, siêu âm tim và các xét nghiệm mỡ máu.', 1200000.00, 'https://img.freepik.com/free-photo/cardiologist-consulting-patient_23-2148816193.jpg', NOW()),
('Gói khám Nhi khoa định kỳ', 'Kiểm tra sức khỏe, sự phát triển và tư vấn dinh dưỡng, tiêm chủng cho trẻ em.', 500000.00, 'https://img.freepik.com/free-photo/pediatrician-visiting-kid_23-2148825227.jpg', NOW()),
('Gói tầm soát Dạ dày & Đại tràng', 'Tầm soát các bệnh lý tiêu hóa, phát hiện sớm polyp và ung thư đường tiêu hóa.', 3000000.00, 'https://img.freepik.com/free-photo/doctor-examining-patient-digital-tablet_1150-15335.jpg', NOW()),
('Gói khám Răng - Hàm - Mặt', 'Kiểm tra sức khỏe răng miệng tổng quát, lấy cao răng và tư vấn thẩm mỹ nha khoa.', 700000.00, 'https://img.freepik.com/free-photo/dentist-working-patient_23-2148825232.jpg', NOW()),
('Gói chăm sóc Da liễu Nâng cao', 'Khám và tư vấn các bệnh lý về da, tầm soát ung thư da và tư vấn liệu trình chăm sóc da chuyên sâu.', 1000000.00, 'https://img.freepik.com/free-photo/dermatologist-examining-patient-skin_23-2148816223.jpg', NOW());

-- Generate Schedules for the 10 most recently added packages
-- Setting all schedules for a single day: 2026-05-01
-- Time: 08:00 to 18:00 (every 30 mins)

INSERT INTO health_package_schedules (health_package_id, work_date, start_time, end_time, created_at)
SELECT 
    pkg.id,
    '2026-05-01',
    TIME(ADDTIME('08:00:00', SEC_TO_TIME(t.n * 1800))),
    TIME(ADDTIME('08:30:00', SEC_TO_TIME(t.n * 1800))),
    NOW()
FROM 
    (SELECT id FROM health_packages ORDER BY id DESC LIMIT 10) AS pkg
CROSS JOIN 
    (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL 
     SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL 
     SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL 
     SELECT 15 UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19) AS t;
