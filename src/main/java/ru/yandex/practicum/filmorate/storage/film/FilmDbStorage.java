package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.dbException.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public SqlRowSet findAll() {
        String sql = "SELECT * FROM films ORDER BY film_id";

        return jdbcTemplate.queryForRowSet(sql);
    }

    @Override
    public SqlRowSet findFilm(int id) {
        String sql = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        return jdbcTemplate.queryForRowSet(sql, id);
    }

    @Override
    public Integer create(Film film) {
        String sql = "INSERT INTO FILMS (MPA_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setInt(1, film.getMpa().getId());
            ps.setString(2, film.getName());
            ps.setString(3, film.getDescription());
            ps.setDate(4, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(5, film.getDuration());
            return ps;
        }, keyHolder);

        updateFilmGenres(film.getId(), film.getGenres());

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    private void updateFilmGenres(int filmId, Set<Genre> genres) {
        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID =?", filmId);
        log.warn("Film Genres cleared");
        if (genres != null) {
            for (Genre genre : genres) {
                if (!jdbcTemplate.queryForRowSet("SELECT GENRE_ID FROM genres WHERE GENRE_ID =?", genre.getId()).next()) {
                    throw new GenreNotFoundException(String.format("Genre with id = %d not found", genre.getId()));
                }
                jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID =? AND GENRE_ID=?", filmId, genre.getId());
                jdbcTemplate.update("INSERT INTO FILM_GENRES (film_id, genre_id) VALUES (?,?)", filmId, genre.getId());
                genre.setName((String) jdbcTemplate.queryForObject("SELECT name FROM genres WHERE GENRE_ID =?", new Object[]{genre.getId()}, String.class));
            }
        }
    }

    @Override
    public Integer update(Film film) {
        String sql = "UPDATE films SET NAME =?, DESCRIPTION =?, RELEASE_DATE =?, DURATION =?, MPA_ID =? WHERE FILM_ID =?";
        updateFilmGenres(film.getId(), film.getGenres());

        return jdbcTemplate.update(sql, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpa().getId(), film.getId());


//
//        String sql = "UPDATE films SET NAME =?, DESCRIPTION =?, RELEASE_DATE =?, DURATION =?, MPA_ID =? WHERE FILM_ID =?";
//
//        jdbcTemplate.update(sql, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()),
//                film.getDuration(), film.getMpa().getId(), film.getId());
//
//        film.getMpa().setName((String) jdbcTemplate.queryForObject("SELECT NAME FROM MPA WHERE MPA_ID =?",
//                new Object[]{film.getMpa().getId()}, String.class));
//        updateFilmGenres(film.getId(), film.getGenres());
//        film.setGenres(genreService.getGenresOfFilm(film.getId()));
//        return film;
    }

    public SqlRowSet getFilmMpaFromDB(int filmId) {
        String sql = "SELECT MPA.MPA_ID, MPA.NAME FROM MPA LEFT JOIN FILMS F ON MPA.MPA_ID = F.MPA_ID WHERE FILM_ID = ?";
        return jdbcTemplate.queryForRowSet(sql, filmId);
    }
//
//    private Set<Genre> getGenresFromDB(int filmId) {
//        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT fg.GENRE_ID, name FROM FILM_GENRES as fg " +
//                "LEFT JOIN GENRES g on fg.GENRE_ID = g.GENRE_ID " +
//                "WHERE FILM_ID =?", filmId);
//
//        Set<Genre> genres = genreService.getGenresOfFilm(filmId);
//        return genres;
//    }

    @Override
    public void like(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM FILM_LIKES WHERE FILM_ID=? AND USER_ID=?", filmId, userId);
        jdbcTemplate.update("INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?,?)", filmId, userId);
    }

    @Override
    public void unlike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM FILM_LIKES WHERE FILM_ID=? AND USER_ID=?", filmId, userId);
    }

    @Override
    public SqlRowSet getPopular(int limitTo) {
        String sql = "SELECT * FROM " +
                "(SELECT f.FILM_ID, " +
                "COUNT(l.film_id) AS likes " +
                "FROM films as f LEFT JOIN FILM_LIKES as l ON f.FILM_ID = l.film_id " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY likes DESC " +
                "LIMIT " + limitTo + ")";

        return jdbcTemplate.queryForRowSet(sql);

    }
}
