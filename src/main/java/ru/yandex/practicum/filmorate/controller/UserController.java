package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User create(@Valid  @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        user.setId(getNextId());
        setName(user);
        users.put(user.getId(), user);

        log.info("Пользователь с id = {} успешно создан: {}", user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя: {}", user);

        if (!users.containsKey(user.getId())) {
            log.error("Попытка обновления несуществующего пользователя с id = {}", user.getId());
            throw new UserNotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }

        setName(user);
        users.put(user.getId(), user);
        log.info("Пользователь с id = {} успешно обновлён: {}", user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return new ArrayList<>(users.values());
    }

    private void setName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}