ALTER TABLE boards
ADD COLUMN user_ids INT[] DEFAULT '{}',
ADD COLUMN amount_of_users INT DEFAULT 1,
ADD COLUMN number_of_strokes INT DEFAULT 0;