package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: нет email или он не содержит @");
            throw new ValidationException("Email не может быть пустым и должен содержать @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации: нет логина или он содержит пробелы");
            throw new ValidationException("Логин пользователя не может быть пустым или содержать пробелы");
        }

        if (user.getName() == null) {
            log.info("Не указано имя для отображения, в качестве имени будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(Instant.now())) {
            log.warn("Ошибка валидации: указана ещё не наступившая дата");
            throw new ValidationException("Вы путешественник во времени? ;-) Дата рождения не может быть из будущего");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь с id = {} успешно создан: {}", user.getId(), user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (users.containsKey(newUser.getId())) {
            log.info("Обновление пользователя с id = {}", newUser.getId());
            User oldUser = users.get(newUser.getId());

            if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
                oldUser.setEmail(newUser.getEmail());
            }

            if (newUser.getLogin() != null && !newUser.getLogin().equals(oldUser.getLogin())) {
                oldUser.setLogin(newUser.getLogin());
            }

            if (newUser.getName() != null && !newUser.getName().equals(oldUser.getName())) {
                oldUser.setName(newUser.getName());
            }

            if (newUser.getBirthday() != null && !newUser.getBirthday().equals(oldUser.getBirthday())) {
                oldUser.setBirthday(newUser.getBirthday());
            }

            log.info("Пользователь с id = {} успешно обновлён: {}", newUser.getId(), newUser);
            return oldUser;
        }

        log.warn("Попытка обновления несуществующего пользователя с id = {}", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }
}
