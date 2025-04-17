package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.NotBeforeReleaseDate;
import ru.yandex.practicum.filmorate.validator.ValidatorNotBeforeReleaseDate;

import java.lang.annotation.Annotation;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationTest {
    private User user;
    private Film film;
    private ValidatorNotBeforeReleaseDate releaseDateValidator;

    @BeforeEach
    public void setUp() {
        user = new User(null, null, "email@example.ru", "login", LocalDate.of(1997, 3, 10));
        film = new Film(null, LocalDate.of(2017, 9, 14), "name of film", "description of film", 120);
        releaseDateValidator = new ValidatorNotBeforeReleaseDate();
        releaseDateValidator.initialize(new NotBeforeReleaseDate() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public String message() {
                return "Дата релиза не может быть раньше 28 декабря 1895 года";
            }

            @Override
            public String value() {
                return "1895-12-28";
            }
        });
    }

    @Test
    public void testValidUser() {
        assertNotNull(user.getEmail(), "Email не должен быть пустым");
        assertTrue(user.getLogin().matches("\\S+"), "Логин не должен содержать пробелов");
        assertTrue(user.getBirthday().isBefore(LocalDate.now()) || user.getBirthday().isEqual(LocalDate.now()), "Дата рождения не должна быть из будущего");
    }

    @Test
    void testInvalidUser() {
        user.setEmail(null);
        user.setLogin(" ");
        user.setBirthday(LocalDate.of(3000, 1, 1));

        assertThrows(IllegalArgumentException.class, () -> {
            if (user.getEmail() == null) throw new IllegalArgumentException("Email не должен быть пустым");
            if (!user.getLogin().matches("\\S+")) throw new IllegalArgumentException("Логин не должен содержать пробелы");
            if (user.getBirthday().isAfter(LocalDate.now())) throw new IllegalArgumentException("Дата рождения не должна быть из будущего");
        });
    }

    @Test
    void testValidFilm() {
        assertTrue(releaseDateValidator.isValid(film.getReleaseDate(), null), "Дата релиза не может быть раньше 28 декабря 1895 года");
        assertFalse(film.getName().isEmpty(), "Название фильма не должно быть пустым");
        assertTrue(film.getDescription().length() <= 200, "Описание фильма не должно превышать 200 символов");
        assertTrue(film.getDuration() > 0, "Длительность фильма должна быть положительной");
    }

    @Test
    void testInvalidFilm() {
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setName("");
        film.setDescription("1".repeat(201));
        film.setDuration(-1);

        assertThrows(IllegalArgumentException.class, () -> {
            if (!releaseDateValidator.isValid(film.getReleaseDate(), null)) throw new IllegalArgumentException("Дата релиза не может быть раньше 28 декабря 1895 года");
            if (film.getName().isEmpty()) throw new IllegalArgumentException("Название не должно быть пустым");
            if (film.getDescription().length() > 200) throw new IllegalArgumentException("Описание не должно превышать 200 символов");
            if (film.getDuration() <= 0) throw new IllegalArgumentException("Длительность должна быть положительной");
        });
    }
}
