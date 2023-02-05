package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exception.dbException.IllegalGenreException;
import ru.yandex.practicum.filmorate.exception.dbException.IllegalMpaException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.sql.PreparedStatement;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {


    private final HashMap<Integer, Film> films = new HashMap<>();

    private final JdbcTemplate jdbcTemplate;

    private int id = 0;

    public List<Film> findAll() {
        List<Film> result = new ArrayList<>();
        String sql = "SELECT film_id FROM films";
        List<Integer> ids = jdbcTemplate.queryForList(sql, Integer.class);
        for (Integer id : ids) {
            result.add(findFilm(id));
        }
        return result;
    }

    public Film findFilm(int id) {
        String sql = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        SqlRowSet urs = jdbcTemplate.queryForRowSet(sql, id);

        if (!urs.next()) {
            throw new ElementNotFoundException(String.format("Film %d not found", id));
        }
        System.out.println(urs);
        Film film = Film.builder()
                .id(id)
                .name(urs.getString("name"))
                .description(urs.getString("description"))
                .releaseDate(Objects.requireNonNull(urs.getDate("release_date")).toLocalDate())
                .duration(urs.getInt("duration"))
                .genres(getGenresFromDB(id))
                .mpa(getMpaFromDB(id))
                .build();


        return film;
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
            genres[i] = new Genre(genreID, genresMap.get(genreID));
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
        return new Mpa(mpaId, mpaName);
    }


    public Film create(Film film) {
        if (!jdbcTemplate.queryForRowSet("SELECT NAME FROM MPA WHERE MPA_ID =?", new Object[]{film.getMpa().getId()}).next()) {
            throw new IllegalMpaException("Mpa with id: " + film.getMpa().getId() + " not found");
        }

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
        Genre[] genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                if (!jdbcTemplate.queryForRowSet("SELECT NAME FROM GENRES WHERE GENRE_ID =?", genre.getId()).next()) {
                    throw new IllegalGenreException(String.format("Genres with id = %d not found", genre.getId()));
                }
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?,?)", generatedId, genre.getId());
            }
            for (Genre genre : genres) {
                genre.setName((String) jdbcTemplate.queryForObject("SELECT name FROM genres WHERE GENRE_ID =?", new Object[]{genre.getId()}, String.class));
            }
        }
        return film;
    }

    public Film update(Film film) {
        if (!jdbcTemplate.queryForRowSet("SELECT NAME FROM MPA WHERE MPA_ID =?", new Object[]{film.getMpa().getId()}).next()) {
            throw new IllegalMpaException("Mpa with id: " + film.getMpa().getId() + " not found");
        }
        if (!jdbcTemplate.queryForRowSet("SELECT FILM_ID FROM FILMS WHERE FILM_ID =?", film.getId()).next()) {
            throw new ElementNotFoundException("Film with id: " + film.getId() + " not found");
        }

        String sql = "UPDATE films SET NAME =?, DESCRIPTION =?, RELEASE_DATE =?, DURATION =?, MPA_ID =? WHERE FILM_ID =?";

        jdbcTemplate.update(sql, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpa().getId(), film.getId());

        film.getMpa().setName((String) jdbcTemplate.queryForObject("SELECT NAME FROM MPA WHERE MPA_ID =?",
                new Object[]{film.getMpa().getId()}, String.class));
        Genre[] genres = film.getGenres();
        if (genres == null) {
            return film;
        }

        for (Genre genre : genres) {
            System.out.println(genre);
            if (!jdbcTemplate.queryForRowSet("SELECT GENRE_ID FROM genres WHERE GENRE_ID =?", genre.getId()).next()) {
                throw new IllegalGenreException(String.format("Genres with id = %d not found", genre.getId()));
            }
            jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?,?)", film.getId(), genre.getId());
        }
        for (Genre genre : genres) {
            genre.setName((String) jdbcTemplate.queryForObject("SELECT name FROM genres WHERE GENRE_ID =?",
                    new Object[]{genre.getId()}, String.class));
        }

        return film;
    }
}
