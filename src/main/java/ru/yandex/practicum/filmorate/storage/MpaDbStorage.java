package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;
import ru.yandex.practicum.filmorate.util.Constants;

import java.util.List;

@Repository
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query(Constants.SQL_SELECT_ALL_MPA, EntityMapper::mapRowToMpa);
    }

    @Override
    public Mpa getMpaById(int id) {
        try {
            return jdbcTemplate.queryForObject(Constants.SQL_SELECT_MPA, EntityMapper::mapRowToMpa, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(String.format(Constants.ERROR_MPA_NOT_FOUND, id));
        }
    }
}