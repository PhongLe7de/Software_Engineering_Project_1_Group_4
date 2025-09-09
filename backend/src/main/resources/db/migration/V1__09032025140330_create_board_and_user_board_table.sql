CREATE TABLE boards
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    owner_id   INT  NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE user_boards
(
    id       SERIAL PRIMARY KEY ,
    user_id  INT NOT NULL,
    board_id INT NOT NULL,
    role     VARCHAR(50) DEFAULT 'EDITOR',
    UNIQUE (user_id, board_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_board FOREIGN KEY (board_id) REFERENCES boards (id)
);