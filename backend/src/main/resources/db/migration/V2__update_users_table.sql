-- Update users table to match new authentication schema
ALTER TABLE users DROP COLUMN IF EXISTS uid;
ALTER TABLE users ADD COLUMN IF NOT EXISTS password VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS display_name VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS photo_url TEXT;

-- Update password column to NOT NULL after adding it
-- (We'll need to add a default password for existing users if any)
UPDATE users SET password = '$2a$10$defaultHashedPassword' WHERE password IS NULL;
ALTER TABLE users ALTER COLUMN password SET NOT NULL; 