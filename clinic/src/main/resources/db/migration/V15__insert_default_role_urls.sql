-- Insert Admin permissions (Full access)
INSERT INTO role_urls (role_id, url_pattern, http_method, description)
SELECT r.id, p.pattern, m.method, 'Admin full access'
FROM roles r
CROSS JOIN (SELECT '/**' as pattern) p
CROSS JOIN (SELECT 'GET' as method UNION ALL SELECT 'POST' UNION ALL SELECT 'PUT' UNION ALL SELECT 'DELETE') m
WHERE r.name = 'ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM role_urls ru WHERE ru.role_id = r.id AND ru.url_pattern = p.pattern AND ru.http_method = m.method
);

-- Insert Doctor permissions
INSERT INTO role_urls (role_id, url_pattern, http_method, description)
SELECT r.id, p.pattern, m.method, 'Doctor access'
FROM roles r
CROSS JOIN (
    SELECT '/appointments/**' as pattern UNION ALL
    SELECT '/schedules/**' UNION ALL
    SELECT '/medical-records/**' UNION ALL
    SELECT '/prescriptions/**' UNION ALL
    SELECT '/lab-orders/**'
) p
CROSS JOIN (SELECT 'GET' as method UNION ALL SELECT 'POST' UNION ALL SELECT 'PUT' UNION ALL SELECT 'DELETE') m
WHERE r.name = 'DOCTOR'
AND NOT EXISTS (
    SELECT 1 FROM role_urls ru WHERE ru.role_id = r.id AND ru.url_pattern = p.pattern AND ru.http_method = m.method
);

-- Insert Employee permissions
INSERT INTO role_urls (role_id, url_pattern, http_method, description)
SELECT r.id, p.pattern, m.method, 'Employee access'
FROM roles r
CROSS JOIN (
    SELECT '/health-packages/**' as pattern UNION ALL
    SELECT '/specializations/**' UNION ALL
    SELECT '/services/**'
) p
CROSS JOIN (SELECT 'GET' as method UNION ALL SELECT 'POST' UNION ALL SELECT 'PUT' UNION ALL SELECT 'DELETE') m
WHERE r.name = 'EMPLOYEE'
AND NOT EXISTS (
    SELECT 1 FROM role_urls ru WHERE ru.role_id = r.id AND ru.url_pattern = p.pattern AND ru.http_method = m.method
);
