-- Normalize url_pattern: /api/module/** -> /module/** (canonical form for matrix + filter)
UPDATE role_urls
SET url_pattern = SUBSTRING(url_pattern, 5)
WHERE url_pattern LIKE '/api/%'
  AND url_pattern != '/api/**';
