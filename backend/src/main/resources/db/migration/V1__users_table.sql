CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) ,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);