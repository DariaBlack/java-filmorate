package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма: {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с id = {} успешно создан: {}", film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма: {}", film);

        if (!films.containsKey(film.getId())) {
            log.error("Попытка обновления несуществующего фильма с id = {}", film.getId());
            throw new ValidationException("Фильм с id = " + film.getId() + " не найден");
        }

        films.put(film.getId(), film);
        log.info("Фильм с id = {} успешно обновлён: {}", film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return new ArrayList<>(films.values());
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}