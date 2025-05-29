package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ValidatorNotBeforeReleaseDate implements ConstraintValidator<NotBeforeReleaseDate, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(NotBeforeReleaseDate constraintAnnotation) {
        minDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(minDate);
    }
}