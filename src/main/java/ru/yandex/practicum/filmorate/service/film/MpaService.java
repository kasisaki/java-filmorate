package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.dbException.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> findAll() {
        String sql = "SELECT * FROM MPA";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("name"))
                .build()));
    }

    public Mpa findMpa(int id) {
        List<Mpa> mpas =  jdbcTemplate.query("SELECT * FROM MPA WHERE MPA_ID =" + id , ((rs, rowNum) -> Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("name"))
                .build()));
        if (mpas.size() == 0) {
            throw new MpaNotFoundException(String.format("Mpa with id %d not found", id));
        }
        return mpas.get(0);
    }
}
