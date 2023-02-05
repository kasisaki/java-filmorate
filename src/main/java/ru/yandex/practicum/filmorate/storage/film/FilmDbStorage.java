package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exception.dbException.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.dbException.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.sql.PreparedStatement;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        List<Film> result = new ArrayList<>();
        String sql = "SELECT film_id FROM films ORDER BY film_id";
        List<Integer> ids = jdbcTemplate.queryForList(sql, Integer.class);
        for (Integer id : ids) {
            result.add(findFilm(id));
        }
        return result;
    }

    @Override
    public Film findFilm(int id) {
        String sql = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);

        if (!rs.next()) {
            throw new ElementNotFoundException(String.format("Film %d not found", id));
        }

        return Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(Objects.requireNonNull(rs.getDate("release_date")).toLocalDate())
                .duration(rs.getInt("duration"))
                .genres(getGenresFromDB(id))
                .mpa(getMpaFromDB(id))
                .build();
    }

    private Genre[] getGenresFromDB(int id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT fg.GENRE_ID, name FROM FILM_GENRES as fg " +
                "LEFT JOIN GENRES g on fg.GENRE_ID = g.GENRE_ID " +
                "WHERE FILM_ID =?", id);

        Map<Integer, String> genresMap = new HashMap<>();

        while (rs.next()) {
            genresMap.put(rs.getInt("GENRE_ID"), rs.getString("name"));
        }
        Genre[] genres = new Genre[genresMap.size()];
        int i = 0;
        for (Integer genreID : genresMap.keySet()) {
            genres[i] = Genre.builder().id(genreID).name(genresMap.get(genreID)).build();
            log.warn(genres[i].toString());
            i++;
        }
        return genres;
    }

    private Mpa getMpaFromDB(int id) {
        String sql = "SELECT MPA.MPA_ID, MPA.NAME FROM MPA LEFT JOIN FILMS F ON MPA.MPA_ID = F.MPA_ID WHERE FILM_ID = ?";
        SqlRowSet urs = jdbcTemplate.queryForRowSet(sql, id);
        if (!urs.next()) {
            throw new ElementNotFoundException(String.format("Film %d not found", id));
        }

        int mpaId = urs.getInt("MPA_ID");
        String mpaName = urs.getString("NAME");
        return Mpa.builder().id(mpaId).name(mpaName).build();
    }

    @Override
    public Film create(Film film) {
        correctMpaOrThrow(film.getMpa().getId());

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

        int generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();

        film.setId(generatedId);

        film.getMpa().setName((String) jdbcTemplate.queryForObject("SELECT NAME FROM MPA WHERE MPA_ID =?",
                new Object[]{film.getMpa().getId()}, String.class));

        updateFilmGenres(generatedId, film.getGenres());
        return film;
    }

    private void updateFilmGenres(int filmId, Genre[] genres) {
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
    public Film update(Film film) {
        correctMpaOrThrow(film.getMpa().getId());

        if (!jdbcTemplate.queryForRowSet("SELECT FILM_ID FROM FILMS WHERE FILM_ID =?", film.getId()).next()) {
            throw new ElementNotFoundException("Film with id: " + film.getId() + " not found");
        }

        String sql = "UPDATE films SET NAME =?, DESCRIPTION =?, RELEASE_DATE =?, DURATION =?, MPA_ID =? WHERE FILM_ID =?";

        jdbcTemplate.update(sql, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpa().getId(), film.getId());

        film.getMpa().setName((String) jdbcTemplate.queryForObject("SELECT NAME FROM MPA WHERE MPA_ID =?",
                new Object[]{film.getMpa().getId()}, String.class));
        updateFilmGenres(film.getId(), film.getGenres());
        film.setGenres(getGenresFromDB(film.getId()));
        return film;
    }

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
    public List<Film> getPopular(int limitTo) {
        String sql = "SELECT film_ID FROM " +
                "(SELECT f.FILM_ID, " +
                "COUNT(l.film_id) AS likes " +
                "FROM films as f LEFT JOIN FILM_LIKES as l ON f.FILM_ID = l.film_id " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY likes DESC " +
                "LIMIT " + limitTo + ")";

        List<Integer> listOfFilms = jdbcTemplate.queryForList(sql, Integer.class);
        List<Film> films = new ArrayList<>();
        for (Integer filmId : listOfFilms) {
            films.add(findFilm(filmId));
        }
        return films;
    }

    private void correctMpaOrThrow(int mpaId) {
        if (!jdbcTemplate.queryForRowSet("SELECT NAME FROM MPA WHERE MPA_ID =?", mpaId).next()) {
            throw new MpaNotFoundException("Mpa with id: " + mpaId + " not found");
        }

    }
}
