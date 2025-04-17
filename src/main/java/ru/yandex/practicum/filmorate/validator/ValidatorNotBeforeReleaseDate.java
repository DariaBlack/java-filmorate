package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ValidatorNotBeforeReleaseDate implements ConstraintValidator<NotBeforeReleaseDate, LocalDate> {

    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        return releaseDate != null && !releaseDate.isAfter(FIRST_FILM_DATE);
    }
}
