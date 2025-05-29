package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        if (user == null || friend == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);

        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = userStorage.getUser(userId);
        User otherUser = userStorage.getUser(otherUserId);

        Set<Long> commonFriendsIds = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .collect(Collectors.toSet());

        return commonFriendsIds.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }
}
