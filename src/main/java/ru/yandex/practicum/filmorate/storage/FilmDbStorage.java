package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.List;

@Repository
@Qualifier("filmDbStorage")
@Profile("!test")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        Long id = jdbcTemplate.queryForObject("SELECT IDENTITY()", Long.class);
        film.setId(id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        return film;
    }

    @Override
    public Film getFilm(Long id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, new FilmRowMapper(), id);
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, new FilmRowMapper());
    }
}