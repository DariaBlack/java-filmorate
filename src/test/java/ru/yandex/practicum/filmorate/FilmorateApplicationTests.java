package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmorateApplicationTests {

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM film_genre");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
    }

    @Test
    void testAddUser() {
        User user = createTestUser("test@test.com", "testuser", "Test User", LocalDate.of(1990, 1, 1));

        User savedUser = userStorage.addUser(user);

        assertNotNull(savedUser.getId());
        assertEquals("test@test.com", savedUser.getEmail());
        assertEquals("testuser", savedUser.getLogin());
        assertEquals("Test User", savedUser.getName());
        assertEquals(LocalDate.of(1990, 1, 1), savedUser.getBirthday());
    }

    @Test
    void testGetAllUsers() {
        User user1 = createTestUser("user1@test.com", "user1", "User 1", LocalDate.of(1990, 1, 1));
        User user2 = createTestUser("user2@test.com", "user2", "User 2", LocalDate.of(1991, 1, 1));

        userStorage.addUser(user1);
        userStorage.addUser(user2);

        List<User> users = userStorage.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void testUpdateUser() {
        User user = createTestUser("test@test.com", "testuser", "Test User", LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.addUser(user);

        savedUser.setName("Updated Name");
        savedUser.setEmail("updated@test.com");

        User updatedUser = userStorage.updateUser(savedUser);

        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@test.com", updatedUser.getEmail());
    }

    @Test
    void testGetUserById() {
        User user = createTestUser("test@test.com", "testuser", "Test User", LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.addUser(user);

        Optional<User> retrievedUser = userStorage.getUser(savedUser.getId());

        assertTrue(retrievedUser.isPresent());
        assertEquals(savedUser.getId(), retrievedUser.get().getId());
        assertEquals(savedUser.getEmail(), retrievedUser.get().getEmail());
    }

    @Test
    void testAddFriend() {
        User user1 = createTestUser("user1@test.com", "user1", "User 1", LocalDate.of(1990, 1, 1));
        User user2 = createTestUser("user2@test.com", "user2", "User 2", LocalDate.of(1991, 1, 1));

        User savedUser1 = userStorage.addUser(user1);
        User savedUser2 = userStorage.addUser(user2);

        userStorage.addFriend(savedUser1.getId(), savedUser2.getId());

        List<User> friends = userStorage.getFriends(savedUser1.getId());
        assertEquals(1, friends.size());
        assertEquals(savedUser2.getId(), friends.get(0).getId());
    }

    @Test
    void testRemoveFriend() {
        User user1 = createTestUser("user1@test.com", "user1", "User 1", LocalDate.of(1990, 1, 1));
        User user2 = createTestUser("user2@test.com", "user2", "User 2", LocalDate.of(1991, 1, 1));

        User savedUser1 = userStorage.addUser(user1);
        User savedUser2 = userStorage.addUser(user2);

        userStorage.addFriend(savedUser1.getId(), savedUser2.getId());
        userStorage.removeFriend(savedUser1.getId(), savedUser2.getId());

        List<User> friends = userStorage.getFriends(savedUser1.getId());
        assertTrue(friends.isEmpty());
    }

    private User createTestUser(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }
}