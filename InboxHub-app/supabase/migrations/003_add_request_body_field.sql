-- Add request body field configuration to api_configs table
ALTER TABLE api_configs ADD COLUMN request_body_field VARCHAR(100) DEFAULT 'taskContent' NOT NULL;

-- Add comment to explain the field
COMMENT ON COLUMN api_configs.request_body_field IS 'The field name to use in the request body JSON (e.g., taskContent, content, message, text)';