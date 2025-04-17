package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ValidatorNotBeforeReleaseDate implements ConstraintValidator<NotBeforeReleaseDate, LocalDate> {

    private LocalDate minDate;

    @Override
    public void initialize(NotBeforeReleaseDate constraint) {
        minDate = LocalDate.parse(constraint.value());
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        return releaseDate != null && releaseDate.isAfter(minDate);
    }
}
