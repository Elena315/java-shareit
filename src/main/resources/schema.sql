CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT unique_email UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    available boolean NOT NULL,
    owner_id BIGINT,
    request_id BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    start_date timestamp WITHOUT TIME ZONE NOT NULL,
    end_date timestamp WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT,
    booker_id BIGINT,
    status varchar(9) NOT NULL,
    CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(item_id),
    CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    comment_text VARCHAR(512) NOT NULL,
    item_id BIGINT,
    author_id BIGINT,
    created timestamp WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(item_id),
    CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(user_id)
    );