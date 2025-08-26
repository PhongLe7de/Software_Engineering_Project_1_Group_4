ALTER TABLE users ADD COLUMN status VARCHAR(20);

CREATE INDEX idx_user_status ON users(status);