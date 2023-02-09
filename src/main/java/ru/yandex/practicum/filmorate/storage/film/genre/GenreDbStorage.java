package ru.yandex.practicum.filmorate.storage.film.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public SqlRowSet findAllGenres() {
        return jdbcTemplate.queryForRowSet("SELECT * FROM GENRES ORDER BY GENRE_ID");
    }

    public SqlRowSet getGenre(int id) {
        return jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE genre_id =?", id);
    }

    public SqlRowSet getGenresOfFilmFromDB(int filmId) {
        return jdbcTemplate.queryForRowSet("SELECT fg.GENRE_ID, name FROM FILM_GENRES as fg " +
                "LEFT JOIN GENRES g on fg.GENRE_ID = g.GENRE_ID " +
                "WHERE FILM_ID =? ORDER BY fg.GENRE_ID", filmId);
    }
}
