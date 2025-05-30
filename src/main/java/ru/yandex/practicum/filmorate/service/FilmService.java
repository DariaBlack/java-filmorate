package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.util.Constants;

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
        return filmStorage.getFilm(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(Constants.ERROR_FILM_NOT_FOUND, id)));
    }

    public void addLike(Long filmId, Long userId) {
        validateFilmAndUser(filmId, userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        validateFilmAndUser(filmId, userId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateFilmData(Film film) {
        if (film.getMpa() != null) {
            try {
                mpaService.getMpaById(film.getMpa().getId());
            } catch (EntityNotFoundException e) {
                throw new EntityNotFoundException("MPA рейтинг с id " + film.getMpa().getId() + " не найден. Пожалуйста, выберите существующий MPA рейтинг.");
            }
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Integer> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .distinct()
                    .collect(Collectors.toList());

            List<Genre> validGenres = genreService.getGenresByIds(genreIds);

            if (validGenres.size() != genreIds.size()) {
                throw new EntityNotFoundException("Один или несколько жанров не найдены. Пожалуйста, выберите существующие жанры.");
            }

            film.setGenres(validGenres);
        }
    }

    private void validateFilmAndUser(Long filmId, Long userId) {
        getFilm(filmId); // Проверяем существование фильма
        userStorage.getUser(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
