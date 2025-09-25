
INSERT INTO users (id, password, email, display_name, photo_url, status, created_at)
VALUES (1, 'password', 'mail@mail.com', 'Test User', 'https://example.com/photo.jpg', 'ACTIVE', NOW());

-- Insert a board with ID 2, owned by the user with ID 1
INSERT INTO boards (id, name, owner_id, amount_of_users, number_of_strokes, created_at, updated_at)
VALUES (1, 'TEST BOARD', 1, 1, 0, NOW(), NOW());

-- Associate the user with ID 1 to the board with ID 2 in the join table
INSERT INTO board_user_ids (board_id, user_id)
VALUES (1, 1);
