CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                        uid UUID NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       status VARCHAR(20),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);