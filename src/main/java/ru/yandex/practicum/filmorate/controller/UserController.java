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
        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        validateBirthday(user.getBirthday());

        if (user.getName() == null) {
            log.info("Не указано имя для отображения, в качестве имени будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь с id = {} успешно создан: {}", user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null || !users.containsKey(newUser.getId())) {
            log.warn("Попытка обновления несуществующего пользователя с id = {}", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        log.info("Обновление пользователя с id = {}", newUser.getId());
        User oldUser = users.get(newUser.getId());

        if (newUser.getEmail() != null) {
            validateEmail(newUser.getEmail());
            log.info("Email пользователя с id = {} успешно обновлён: {}", newUser.getId(), newUser.getEmail());
            oldUser.setEmail(newUser.getEmail());
        }

        if (newUser.getLogin() != null) {
            validateLogin(newUser.getLogin());
            log.info("Логин пользователя с id = {} успешно обновлён: {}", newUser.getId(), newUser.getLogin());
            oldUser.setLogin(newUser.getLogin());
        }

        if (newUser.getName() != null) {
            log.info("Имя пользователя с id = {} успешно обновлёно: {}", newUser.getId(), newUser.getName());
            oldUser.setName(newUser.getName());
        }

        if (newUser.getBirthday() != null) {
            validateBirthday(newUser.getBirthday());
            log.info("День рождения пользователя с id = {} успешно обновлён: {}", newUser.getId(), newUser.getBirthday());
            oldUser.setBirthday(newUser.getBirthday());
        }

        log.info("Пользователь с id = {} успешно обновлён: {}", oldUser.getId(), oldUser);
        return oldUser;
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            log.warn("Ошибка валидации: нет email или он не содержит @");
            throw new ValidationException("Email не может быть пустым и должен содержать @");
        }
    }

    private void validateLogin(String login) {
        if (login == null || login.isBlank() || login.contains(" ")) {
            log.warn("Ошибка валидации: нет логина или он содержит пробелы");
            throw new ValidationException("Логин пользователя не может быть пустым или содержать пробелы");
        }
    }

    private void validateBirthday(Instant birthday) {
        if (birthday != null && birthday.isAfter(Instant.now())) {
            log.warn("Ошибка валидации: указана ещё не наступившая дата");
            throw new ValidationException("Вы путешественник во времени? ;-) Дата рождения не может быть из будущего");
        }
    }
}