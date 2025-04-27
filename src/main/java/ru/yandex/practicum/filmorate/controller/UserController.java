package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@Valid  @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        return userService.addUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя: {}", user);
        return userService.updateUser(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable @Positive Long userId, @PathVariable @Positive Long friendId) {
        log.info("Получен запрос на добавление пользователя в друзья: {} -> {}", userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable @Positive Long userId, @PathVariable @Positive Long friendId) {
        log.info("Получен запрос на удаление пользователя из друзей: {} -> {}", userId, friendId);
        userService.removeFriend(userId, friendId);
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable @Positive Long id) {
        log.info("Получен запрос на получение пользователя с id: {}", id);
        return userService.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable @Positive Long id) {
        log.info("Получен запрос на получение списка друзей");
        return userService.getUser(id).getFriends().stream()
                .map(userService::getUser)
                .toList();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable @Positive Long id, @PathVariable @Positive Long otherId) {
        log.info("Получен запрос на получение общего списка друзей}");
        return userService.getCommonFriends(id, otherId);
    }
}
