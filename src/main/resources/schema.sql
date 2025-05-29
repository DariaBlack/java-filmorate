DELETE FROM likes;
DELETE FROM film_genre;
DELETE FROM friends;

DELETE FROM films;
DELETE FROM users;
DELETE FROM rating_mpaa;
DELETE FROM genre;

ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS rating_mpaa (
    rating_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films (
    film_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    release_date DATE,
    duration INT,
    rating_id INT,
    FOREIGN KEY (rating_id) REFERENCES rating_mpaa(rating_id)
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS friends (
    user_id INT,
    friend_id INT,
    status INT,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (friend_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS likes (
                                     film_id BIGINT,
                                     user_id BIGINT,
                                     PRIMARY KEY (film_id, user_id),
                                     FOREIGN KEY (film_id) REFERENCES films(film_id),
                                     FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INT,
    genre_id INT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id),
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id)
);