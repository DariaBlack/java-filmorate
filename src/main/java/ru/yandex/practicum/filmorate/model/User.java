package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private Long id;
    private String name;

    @Email
    @NotNull(message = "Email не должен быть пустым")
    private String email;

    @NotBlank(message = "нет логина или он содержит пробелы")
    private String login;

    @PastOrPresent
    @NotNull(message = "Дата рождения не должна быть пустой")
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();

    StatusFriendship statusFriendship = StatusFriendship.PENDING;
}
