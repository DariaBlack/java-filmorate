package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Long id);

    List<Film> getAllFilms();

    List<Film> getPopularFilms(int count);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);
}
