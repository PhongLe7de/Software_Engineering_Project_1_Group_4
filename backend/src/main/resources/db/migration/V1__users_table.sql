CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       display_name VARCHAR(255),
                       photo_url TEXT,
                       status VARCHAR(20),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);