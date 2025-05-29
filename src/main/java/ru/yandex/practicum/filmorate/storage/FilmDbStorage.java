package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getMpa() == null || !ratingExists(film.getMpa().getId())) {
            throw new EntityNotFoundException("MPA рейтинг с id " + (film.getMpa() != null ? film.getMpa().getId() : "null") + " не найден");
        }

        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addGenresToFilm(film.getId(), film.getGenres());
        }

        return getFilm(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addGenresToFilm(film.getId(), film.getGenres());
        }

        return getFilm(film.getId());
    }

    @Override
    public Film getFilm(Long id) {
        String sql = "SELECT f.*, r.name as rating_name FROM films f JOIN rating_mpaa r ON f.rating_id = r.rating_id WHERE f.film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, EntityMapper::mapRowToFilm, id);
            if (film != null) {
                List<Genre> genres = jdbcTemplate.query(
                        "SELECT g.genre_id, g.name FROM film_genre fg " +
                                "JOIN genre g ON fg.genre_id = g.genre_id " +
                                "WHERE fg.film_id = ? " +
                                "ORDER BY g.genre_id",
                        EntityMapper::mapRowToGenre,
                        id
                );
                film.setGenres(genres);
            }
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Фильм с id " + id + " не найден");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.*, r.name as rating_name FROM films f " +
                "JOIN rating_mpaa r ON f.rating_id = r.rating_id " +
                "ORDER BY f.film_id";
        List<Film> films = jdbcTemplate.query(sql, EntityMapper::mapRowToFilm);

        for (Film film : films) {
            List<Genre> genres = jdbcTemplate.query(
                    "SELECT g.genre_id, g.name FROM film_genre fg " +
                            "JOIN genre g ON fg.genre_id = g.genre_id " +
                            "WHERE fg.film_id = ? " +
                            "ORDER BY g.genre_id",
                    EntityMapper::mapRowToGenre,
                    film.getId()
            );
            film.setGenres(genres);
        }

        return films;
    }

    private boolean ratingExists(int ratingId) {
        String sql = "SELECT COUNT(*) FROM rating_mpaa WHERE rating_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, ratingId);
        return count != null && count > 0;
    }

    private void addGenresToFilm(Long filmId, List<Genre> genres) {
        for (Genre genre : genres) {
            jdbcTemplate.update(
                    "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                    filmId, genre.getId()
            );
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, r.name as rating_name, COUNT(l.user_id) as like_count " +
                "FROM films f " +
                "JOIN rating_mpaa r ON f.rating_id = r.rating_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, new Object[]{count}, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("rating_id"));
            mpa.setName(rs.getString("rating_name"));
            film.setMpa(mpa);
            return film;
        });

        for (Film film : films) {
            List<Genre> genres = jdbcTemplate.query(
                    "SELECT g.genre_id, g.name FROM film_genre fg " +
                            "JOIN genre g ON fg.genre_id = g.genre_id " +
                            "WHERE fg.film_id = ? " +
                            "ORDER BY g.genre_id",
                    (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")),
                    film.getId()
            );
            film.setGenres(genres);
        }

        return films;
    }

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }
}
