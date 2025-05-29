//package ru.yandex.practicum.filmorate;
//
//import jakarta.validation.Validation;
//import jakarta.validation.Validator;
//import jakarta.validation.ValidatorFactory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.LocalDate;
//import java.util.HashSet;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ValidationTest {
//    private Validator validator;
//    private User user;
//    private Film film;
//
//    @BeforeEach
//    public void setUp() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//
//        user = new User(null, "Test User", "email@example.ru", "login", LocalDate.of(1997, 3, 10), new HashSet<>());
//        film = new Film(null, LocalDate.of(2017, 9, 14), "name of film", "description of film", 120, new HashSet<>());
//    }
//
//    @Test
//    public void testValidUser() {
//        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
//        assertTrue(violations.isEmpty(), "Валидация пользователя должна проходить без ошибок");
//    }
//
//    @Test
//    void testInvalidUser() {
//        user.setEmail(null);
//        user.setLogin(" ");
//        user.setBirthday(LocalDate.of(3000, 1, 1));
//
//        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
//        assertFalse(violations.isEmpty(), "Пользователь должен содержать ошибки валидации");
//    }
//
//    @Test
//    void testValidFilm() {
//        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
//        assertTrue(violations.isEmpty(), "Валидация фильма должна проходить без ошибок");
//    }
//
//    @Test
//    void testInvalidFilm() {
//        film.setReleaseDate(LocalDate.of(1800, 1, 1));
//        film.setName("");
//        film.setDescription("1".repeat(201));
//        film.setDuration(-1);
//
//        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
//        assertFalse(violations.isEmpty(), "Фильм должен содержать ошибки валидации");
//    }
//}
