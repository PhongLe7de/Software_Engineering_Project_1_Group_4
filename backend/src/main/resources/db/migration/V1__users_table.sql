CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       uid VARCHAR(50),
                       email VARCHAR(255) UNIQUE NOT NULL,
                       display_name VARCHAR(255),
                       photo_url TEXT,
                       status VARCHAR(20),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
); 