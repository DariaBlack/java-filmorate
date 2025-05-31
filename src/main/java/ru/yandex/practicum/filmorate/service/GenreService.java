package ru.yandex.practicum.filmorate.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.Collections;
import java.util.List;

@Service
public class GenreService {

    private final GenreStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper;

    public GenreService(GenreStorage genreStorage, JdbcTemplate jdbcTemplate, GenreMapper genreMapper) {
        this.genreStorage = genreStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.genreMapper = genreMapper;
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }

    public List<Genre> getGenresByIds(List<Integer> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        String sql = "SELECT * FROM genre WHERE genre_id IN (" +
                String.join(",", Collections.nCopies(ids.size(), "?")) + ")";
        return jdbcTemplate.query(sql, ids.toArray(), genreMapper);
    }
}
