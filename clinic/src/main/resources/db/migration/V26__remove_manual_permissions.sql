-- ============================================================
-- V21: Xóa toàn bộ quyền thủ công (MANUAL) khỏi bảng role_urls
-- ============================================================
-- Lý do: Hệ thống chuyển sang phân quyền theo Ma trận (MATRIX).
-- Các quyền MANUAL do DataInitializer cũ tạo ra sẽ bị xóa.
-- DataInitializer mới sẽ seed lại đúng theo catalog với source=MATRIX.
--
-- Rủi ro: Sau khi xóa, STAFF và DOCTOR sẽ mất quyền tạm thời
-- cho đến khi DataInitializer chạy lại sau khi restart ứng dụng.
-- → Giải pháp: Deploy backend và khởi động lại ngay sau migration.
--
-- ADMIN không bị ảnh hưởng vì ADMIN được bypass trong Filter (không cần DB).
-- ============================================================

-- Xóa tất cả quyền được đánh dấu là MANUAL
DELETE FROM role_urls WHERE permission_source = 'MANUAL';

-- Xóa quyền MATRIX của STAFF và DOCTOR (DataInitializer sẽ seed lại đúng)
-- Điều này đảm bảo không bị duplicate khi DataInitializer chạy sau migration.
DELETE FROM role_urls
WHERE permission_source = 'MATRIX'
  AND role_id IN (
      SELECT id FROM roles WHERE name IN ('STAFF', 'DOCTOR')
  );

-- Xóa quyền của PATIENT (không thuộc trang quản lý admin)
DELETE FROM role_urls
WHERE role_id IN (
    SELECT id FROM roles WHERE name = 'PATIENT'
);
