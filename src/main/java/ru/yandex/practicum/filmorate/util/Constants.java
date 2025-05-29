package ru.yandex.practicum.filmorate.util;

public class Constants {
    // SQL-запросы
    public static final String SQL_INSERT_FILM = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    public static final String SQL_UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
    public static final String SQL_SELECT_FILM = "SELECT f.*, r.name as rating_name FROM films f JOIN rating_mpaa r ON f.rating_id = r.rating_id WHERE f.film_id = ?";
    public static final String SQL_SELECT_ALL_FILMS = "SELECT f.*, r.name as rating_name FROM films f JOIN rating_mpaa r ON f.rating_id = r.rating_id ORDER BY f.film_id";
    public static final String SQL_SELECT_FILM_GENRES = "SELECT g.genre_id, g.name FROM film_genre fg JOIN genre g ON fg.genre_id = g.genre_id WHERE fg.film_id = ? ORDER BY g.genre_id";
    public static final String SQL_INSERT_USER = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    public static final String SQL_UPDATE_USER = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE user_id = ?";
    public static final String SQL_SELECT_USER = "SELECT * FROM users WHERE user_id = ?";
    public static final String SQL_SELECT_ALL_USERS = "SELECT * FROM users";
    public static final String SQL_SELECT_ALL_GENRES = "SELECT * FROM genre";
    public static final String SQL_SELECT_GENRE = "SELECT * FROM genre WHERE genre_id = ?";
    public static final String SQL_SELECT_ALL_MPA = "SELECT * FROM rating_mpaa";
    public static final String SQL_SELECT_MPA = "SELECT * FROM rating_mpaa WHERE rating_id = ?";

    // Сообщения об ошибках
    public static final String ERROR_FILM_NOT_FOUND = "Фильм с id %d не найден";
    public static final String ERROR_USER_NOT_FOUND = "Пользователь с id %d не найден";
    public static final String ERROR_GENRE_NOT_FOUND = "Жанр с id %d не найден";
    public static final String ERROR_MPA_NOT_FOUND = "MPA рейтинг с id %d не найден";
}