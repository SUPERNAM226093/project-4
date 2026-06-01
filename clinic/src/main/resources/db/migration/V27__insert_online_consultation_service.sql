-- Migration to insert the online consultation service ("Khám onl") into the services table
-- ID 4 is inserted explicitly to maintain consistency with the fallback ID on the frontend.
INSERT INTO services (id, name, description, price, type, duration_minutes, created_by, feature_image)
VALUES (4, 'Khám onl', 'Khám online qua video call', 200000.00, 'ONLINE', 30, NULL, NULL)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    type = VALUES(type),
    duration_minutes = VALUES(duration_minutes);
