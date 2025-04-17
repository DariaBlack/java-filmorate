package ru.yandex.practicum.filmorate.model;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.Duration;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Film {
    private Long id;
    private Instant releaseDate;

    @NotEmpty(message = "Название не должно быть пустым")
    private String name;

    @Size(max = 200, message = "Длина описания не должна превышать 200 символов")
    private String description;

    @Positive(message = "Длительность должна быть положительной")
    private Duration duration;
}