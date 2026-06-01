-- Update EMPLOYEE to STAFF if it exists
UPDATE roles SET name = 'STAFF' WHERE name = 'EMPLOYEE';

-- Ensure PATIENT role exists
INSERT INTO roles (name)
SELECT 'PATIENT'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'PATIENT');

-- Ensure STAFF role exists (in case EMPLOYEE wasn't there)
INSERT INTO roles (name)
SELECT 'STAFF'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'STAFF');

-- Clear old permissions for STAFF/DOCTOR to re-map according to plan
-- First for STAFF (formerly EMPLOYEE)
DELETE FROM role_urls WHERE role_id IN (SELECT id FROM roles WHERE name = 'STAFF');
-- Then for DOCTOR
DELETE FROM role_urls WHERE role_id IN (SELECT id FROM roles WHERE name = 'DOCTOR');
-- Then for PATIENT
DELETE FROM role_urls WHERE role_id IN (SELECT id FROM roles WHERE name = 'PATIENT');

-- 1. STAFF Permissions (Reducing doctor workload)
-- Staff manages: patients (/users), appointments, rooms, registrations (room-bookings), health packages, lab orders, and doctors list.
INSERT INTO role_urls (role_id, url_pattern, http_method, description)
SELECT r.id, p.pattern, m.method, 'Staff operational access'
FROM roles r
CROSS JOIN (
    SELECT '/api/users/**' as pattern UNION ALL
    SELECT '/api/appointments/**' UNION ALL
    SELECT '/api/rooms/**' UNION ALL
    SELECT '/api/room-bookings/**' UNION ALL
    SELECT '/api/lab-orders/**' UNION ALL
    SELECT '/api/health-packages/**' UNION ALL
    SELECT '/api/doctors/**'
) p
CROSS JOIN (SELECT 'GET' as method UNION ALL SELECT 'POST' UNION ALL SELECT 'PUT' UNION ALL SELECT 'DELETE') m
WHERE r.name = 'STAFF';

-- 2. DOCTOR Permissions (Clinical focus)
-- Doctor focus on: appointments, medical records, prescriptions, lab orders.
INSERT INTO role_urls (role_id, url_pattern, http_method, description)
SELECT r.id, p.pattern, m.method, 'Doctor clinical access'
FROM roles r
CROSS JOIN (
    SELECT '/api/appointments/**' as pattern UNION ALL
    SELECT '/api/medical-records/**' UNION ALL
    SELECT '/api/prescriptions/**' UNION ALL
    SELECT '/api/lab-orders/**'
) p
CROSS JOIN (SELECT 'GET' as method UNION ALL SELECT 'POST' UNION ALL SELECT 'PUT' UNION ALL SELECT 'DELETE') m
WHERE r.name = 'DOCTOR';

-- 3. PATIENT Permissions (Self-service)
-- Patient can access their own data via specific endpoints or general ones with server-side filtering.
INSERT INTO role_urls (role_id, url_pattern, http_method, description)
SELECT r.id, p.pattern, m.method, 'Patient self-service'
FROM roles r
CROSS JOIN (
    SELECT '/api/appointments/**' as pattern UNION ALL
    SELECT '/api/medical-records/**' UNION ALL
    SELECT '/api/prescriptions/**' UNION ALL
    SELECT '/api/lab-orders/**' UNION ALL
    SELECT '/api/health-packages/**'
) p
CROSS JOIN (SELECT 'GET' as method) m
WHERE r.name = 'PATIENT';
