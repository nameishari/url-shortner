ALTER TABLE short_urls
ADD COLUMN visit_count INT NOT NULL DEFAULT 0,
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN last_modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;