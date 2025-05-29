package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreService genreService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, MpaService mpaService, GenreService genreService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public Film addFilm(Film film) {
        validateFilmData(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilmData(film);
        getFilm(film.getId()); // Проверяем существование фильма
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(Long id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new EntityNotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilmData(Film film) {
        if (film.getMpa() != null) {
            try {
                mpaService.getMpaById(film.getMpa().getId());
            } catch (EntityNotFoundException e) {
                throw new EntityNotFoundException("MPA рейтинг с id " + film.getMpa().getId() + " не найден. Пожалуйста, выберите существующий MPA рейтинг.");
            }
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                try {
                    genreService.getGenreById(genre.getId());
                } catch (EntityNotFoundException e) {
                    throw new EntityNotFoundException("Жанр с id " + genre.getId() + " не найден. Пожалуйста, выберите существующий жанр.");
                }
            }
        }
    }
}
