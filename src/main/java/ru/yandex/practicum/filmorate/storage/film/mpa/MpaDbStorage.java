package ru.yandex.practicum.filmorate.storage.film.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public SqlRowSet findAllMpa() {
        return jdbcTemplate.queryForRowSet("SELECT * FROM MPA");
    }

    public SqlRowSet findMpa(int mpaId) {
        return jdbcTemplate.queryForRowSet("SELECT * FROM MPA WHERE MPA_ID =?", mpaId);
    }


}
