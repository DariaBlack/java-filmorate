package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;

@Repository
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM rating_mpaa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("rating_id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        });
    }

    @Override
    public Mpa getMpaById(int id) {
        String sql = "SELECT * FROM rating_mpaa WHERE rating_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Mpa mpa = new Mpa();
                mpa.setId(rs.getInt("rating_id"));
                mpa.setName(rs.getString("name"));
                return mpa;
            }, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("MPA рейтинг с id " + id + " не найден");
        }
    }
}
