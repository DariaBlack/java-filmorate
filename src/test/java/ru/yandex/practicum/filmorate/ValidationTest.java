//package ru.yandex.practicum.filmorate;
//
//import jakarta.validation.Validation;
//import jakarta.validation.Validator;
//import jakarta.validation.ValidatorFactory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.model.Mpa;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
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
//        user = User.builder()
//                .email("email@example.ru")
//                .login("login")
//                .name("Test User")
//                .birthday(LocalDate.of(1997, 3, 10))
//                .build();
//
//        Mpa mpa = new Mpa();
//        mpa.setId(1);
//        mpa.setName("G");
//
//        film = Film.builder()
//                .name("name of film")
//                .description("description of film")
//                .releaseDate(LocalDate.of(2017, 9, 14))
//                .duration(120)
//                .mpa(mpa)
//                .genres(new ArrayList<>())
//                .build();
//    }
//
//    @Test
//    public void testValidUser() {
//        var violations = validator.validate(user);
//        assertTrue(violations.isEmpty(), "Валидация пользователя должна проходить без ошибок");
//    }
//
//    @Test
//    void testInvalidUser() {
//        user.setEmail(null);
//        user.setLogin(" ");
//        user.setBirthday(LocalDate.of(3000, 1, 1));
//
//        var violations = validator.validate(user);
//        assertFalse(violations.isEmpty(), "Пользователь должен содержать ошибки валидации");
//    }
//
//    @Test
//    void testValidFilm() {
//        var violations = validator.validate(film);
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
//        var violations = validator.validate(film);
//        assertFalse(violations.isEmpty(), "Фильм должен содержать ошибки валидации");
//    }
//}