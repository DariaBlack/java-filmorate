package ru.yandex.practicum.filmorate.storage.inMemoryStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("inMemoryUserStorage")
@Profile("test")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с id = {} успешно создан: {}", user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new EntityNotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id = {} успешно обновлён: {}", user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        if (user == null || friend == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        user.getFriends().add(friendId);
        log.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = users.get(userId);

        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        user.getFriends().remove(friendId);
        log.info("Пользователь с id = {} удалил из друзей пользователя с id = {}", userId, friendId);
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
