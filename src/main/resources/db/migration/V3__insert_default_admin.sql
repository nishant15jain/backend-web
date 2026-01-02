-- Insert default admin user
-- Password: admin123 (plain text)
INSERT INTO users (name, email, password_hash, role, created_at) 
VALUES ('Admin', 'admin@pharma.com', 'admin123', 'ADMIN', NOW())
ON DUPLICATE KEY UPDATE name = name;

