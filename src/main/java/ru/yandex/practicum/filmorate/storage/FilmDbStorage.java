package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.util.Constants;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private final GenreMapper genreMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMapper filmMapper, GenreMapper genreMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMapper = filmMapper;
        this.genreMapper = genreMapper;
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getMpa() == null || !ratingExists(film.getMpa().getId())) {
            throw new EntityNotFoundException(String.format(Constants.ERROR_MPA_NOT_FOUND, film.getMpa() != null ? film.getMpa().getId() : "null"));
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(Constants.SQL_INSERT_FILM, Statement.RETURN_GENERATED_KEYS);
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

        return getFilm(film.getId()).orElseThrow(() -> new EntityNotFoundException(String.format(Constants.ERROR_FILM_NOT_FOUND, film.getId())));
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(Constants.SQL_UPDATE_FILM,
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addGenresToFilm(film.getId(), film.getGenres());
        }

        return getFilm(film.getId()).orElseThrow(() -> new EntityNotFoundException(String.format(Constants.ERROR_FILM_NOT_FOUND, film.getId())));
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        try {
            Film film = jdbcTemplate.queryForObject(Constants.SQL_SELECT_FILM, filmMapper, id);
            if (film != null) {
                List<Genre> genres = jdbcTemplate.query(Constants.SQL_SELECT_FILM_GENRES, genreMapper, id);
                film.setGenres(genres);
            }
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.*, r.name as rating_name " +
                "FROM films f " +
                "JOIN rating_mpaa r ON f.rating_id = r.rating_id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = filmMapper.mapRow(rs, rowNum);
            film.setGenres(getGenresForFilm(film.getId()));
            return film;
        });
    }

    private boolean ratingExists(int ratingId) {
        String sql = "SELECT COUNT(*) FROM rating_mpaa WHERE rating_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, ratingId);
        return count != null && count > 0;
    }

    private void addGenresToFilm(Long filmId, List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        List<Object[]> batchArgs = genres.stream()
                .map(genre -> new Object[]{filmId, genre.getId()})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, r.name as rating_name, " +
                "(SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) as like_count " +
                "FROM films f " +
                "JOIN rating_mpaa r ON f.rating_id = r.rating_id " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, new Object[]{count}, (rs, rowNum) -> {
            Film film = filmMapper.mapRow(rs, rowNum);
            film.setGenres(getGenresForFilm(film.getId()));
            return film;
        });
    }

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private List<Genre> getGenresForFilm(Long filmId) {
        String sql = "SELECT g.genre_id, g.name " +
                "FROM film_genre fg " +
                "JOIN genre g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";
        return jdbcTemplate.query(sql, genreMapper, filmId);
    }
}
