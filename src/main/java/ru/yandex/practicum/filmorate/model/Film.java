package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validator.NotBeforeReleaseDate;

import java.time.LocalDate;
import java.util.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Slf4j
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    @Size(max = 200, message = "Название фильма не может быть длиннее 200 символов")
    private String name;
    private String description;

    @NotBeforeReleaseDate(value = "1895-12-28", message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    @NotNull(message = "Дата релиза не может быть пустой")
    private LocalDate releaseDate;

    private Integer duration;
    private final Set<Long> likes = new HashSet<>();
    private Mpa mpa;

    @Builder.Default
    private List<Genre> genres = new ArrayList<>();

    public void setGenres(List<Genre> genres) {
        this.genres = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("film_id", id);
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate.toString());
        values.put("duration", duration);
        values.put("rating_id", mpa == null ? null : mpa.getId());
        return values;
    }
}
