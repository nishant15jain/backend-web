-- Insert default admin user
-- Password: admin123 (BCrypt hashed)
-- Note: $$ is used instead of $ to escape the dollar sign for Railway deployment
INSERT INTO users (name, email, password_hash, role, created_at) 
VALUES ('Admin', 'admin@pharma.com', '$$2a$$10$$0qmD6ayEFfT5oySgiCR2H.zM3O6MzemZ2kFmbNj285R7E8.bXQ9je', 'ADMIN', NOW())
ON DUPLICATE KEY UPDATE name = name;

