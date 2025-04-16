package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateName(film.getName());
        validateDescription(film.getDescription());
        validateReleaseDate(film.getReleaseDate());
        validateDuration(film.getDuration());

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм с id = {} успешно создан: {}", film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null || !films.containsKey(newFilm.getId())) {
            log.warn("Попытка обновления несуществующего фильма с id = {}", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        log.info("Обновление фильма с id = {}", newFilm.getId());
        Film oldFilm = films.get(newFilm.getId());

        if (newFilm.getName() != null) {
            validateName(newFilm.getName());
            oldFilm.setName(newFilm.getName());
            log.info("Название фильма с id = {} успешно обновлено на: {}", newFilm.getId(), newFilm.getName());
        }

        if (newFilm.getDescription() != null) {
            validateDescription(newFilm.getDescription());
            log.info("Описание фильма с id = {} успешно обновлено", newFilm.getId());
            oldFilm.setDescription(newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null) {
            validateReleaseDate(newFilm.getReleaseDate());
            log.info("Дата релиза фильма с id = {} успешно обновлена", newFilm.getId());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() != null) {
            validateDuration(newFilm.getDuration());
            log.info("Продолжительность фильма с id = {} успешно обновлена");
            oldFilm.setDuration(newFilm.getDuration());
        }

        log.info("Фильм с id = {} успешно обновлён: {}", oldFilm.getId(), oldFilm);
        return oldFilm;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            log.warn("Ошибка валидации: нет названия фильма");
            throw new ValidationException("Название фильма не может быть пустым");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > 200) {
            log.warn("Ошибка валидации: описание фильма длиннее 200 символов");
            throw new ValidationException("Описание фильма не должно превышать 200 символов");
        }
    }

    private void validateReleaseDate(Instant releaseDate) {
        if (releaseDate != null && releaseDate.isBefore(ZonedDateTime
                .of(1895, 12, 28, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant())) {
            log.warn("Ошибка валидации: дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private void validateDuration(Duration duration) {
        if (duration != null && duration.isNegative()) {
            log.warn("Ошибка валидации: продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }
}