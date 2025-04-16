package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка валидации: нет названия фильма");
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.warn("Ошибка валидации: слишком длинное описание фильма");
            throw new ValidationException("Описание фильма не должно превышать 200 символов");
        }

        if (film.getReleaseDate().isBefore(ZonedDateTime
                .of(1895, 12, 28, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant())) {
            log.warn("Ошибка валидации: дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration().isNegative()) {
            log.warn("Ошибка валидации: отрицательная продолжительность фильма");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм с id = {} успешно создан: {}", film.getId(), film);
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            log.info("Обновление фильма с id = {}", newFilm.getId());
            Film oldFilm = films.get(newFilm.getId());

            if (newFilm.getName() != null && !newFilm.getName().equals(oldFilm.getName())) {
                oldFilm.setName(newFilm.getName());
            }

            if (newFilm.getDescription() != null && !newFilm.getDescription().equals(oldFilm.getDescription())) {
                oldFilm.setDescription(newFilm.getDescription());
            }

            if (newFilm.getReleaseDate() != null && !newFilm.getReleaseDate().equals(oldFilm.getReleaseDate())) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }

            if (newFilm.getDuration() != null && !newFilm.getDuration().equals(oldFilm.getDuration())) {
                oldFilm.setDuration(newFilm.getDuration());
            }

            log.info("Фильм с id = {} успешно обновлён: {}", newFilm.getId(), newFilm);
            return oldFilm;
        }

        log.warn("Попытка обновления несуществующего фильма с id = {}", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }
}
