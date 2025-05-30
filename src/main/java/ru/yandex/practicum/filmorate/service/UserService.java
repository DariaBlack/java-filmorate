package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (!userStorage.userExists(user.getId())) {
            throw new EntityNotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(Long id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        User user = getUser(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = getUser(userId);
        User otherUser = getUser(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId);
    }
}