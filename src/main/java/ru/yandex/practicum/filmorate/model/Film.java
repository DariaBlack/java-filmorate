package ru.yandex.practicum.filmorate.model;

import lombok.*;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.validator.NotBeforeReleaseDate;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Film {
    private Long id;

    @NotBeforeReleaseDate("1895-12-28")
    private LocalDate releaseDate;

    @NotEmpty(message = "Название не должно быть пустым")
    private String name;

    @Size(max = 200, message = "Длина описания не должна превышать 200 символов")
    private String description;

    @Positive(message = "Длительность должна быть положительной")
    private int duration;
}