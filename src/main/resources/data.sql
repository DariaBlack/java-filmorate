-- Добавление пользователей
INSERT INTO users (name, email, login, birthday) VALUES
    ('Иван Иванов', 'ivanov@example.com', 'ivanov', '1990-01-01'),
    ('Мария Петрова', 'petrova@example.com', 'petrova', '1992-02-02');

-- Добавление рейтингов MPAA
INSERT INTO rating_mpaa (name) VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17');

-- Добавление фильмов
INSERT INTO films (name, description, release_date, duration, rating_id) VALUES
    ('Фильм 1', 'Описание фильма 1', '2020-01-01', 120, 1),
    ('Фильм 2', 'Описание фильма 2', '2021-02-02', 90, 2);

-- Добавление жанров
INSERT INTO genre (name) VALUES
    ('Комедия'),
    ('Драма'),
    ('Триллер');

-- Добавление связей фильмов и жанров
INSERT INTO film_genre (film_id, genre_id) VALUES
    (1, 1),
    (1, 2),
    (2, 3);

-- Добавление друзей
INSERT INTO friends (user_id, friend_id, status) VALUES
    (1, 2, 1);

-- Добавление лайков
INSERT INTO likes (user_id, film_id) VALUES
    (1, 1),
    (2, 2);