CREATE TABLE strokes
(
    id         SERIAL PRIMARY KEY,
    board_id   INT         NOT NULL,
    user_id    INT         NOT NULL,
    color      VARCHAR(50),
    thickness  INT,
    type       VARCHAR(50) NOT NULL,
    tool       VARCHAR(50),
    x_cord     FLOAT,
    y_cord     FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stroke_board FOREIGN KEY (board_id) REFERENCES boards (id),
    CONSTRAINT fk_stroke_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE event_logs
(
    id         SERIAL PRIMARY KEY,
    board_id   INT         NOT NULL,
    user_id    INT         NOT NULL,
    type       VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_board FOREIGN KEY (board_id) REFERENCES boards (id),
    CONSTRAINT fk_event_user FOREIGN KEY (user_id) REFERENCES users (id)
);
