package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidatorNotBeforeReleaseDate.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBeforeReleaseDate {
    String message() default "Дата релиза не может быть раньше {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String value();
}
