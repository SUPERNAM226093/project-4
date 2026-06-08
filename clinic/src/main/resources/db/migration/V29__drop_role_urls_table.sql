-- ============================================================
-- V29: Xóa bảng role_urls (phân quyền động) khỏi Database
-- ============================================================
-- Lý do: Hệ thống chuyển sang phân quyền cố định (hardcode) trong SecurityConfig.
-- Bảng role_urls không còn được sử dụng.
-- ============================================================
DROP TABLE IF EXISTS role_urls;
