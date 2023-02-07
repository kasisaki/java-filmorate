package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreService genreService;
    private final MpaService mpaService;

    public List<Film> findAll() {
        return buildFilmsList(userStorage.findAll());
    }

    public Film findFilm(int id) {
        SqlRowSet urs = filmStorage.findFilm(id);
        if (!urs.next()) {
            throw new ElementNotFoundException(String.format("Film %d not found", id));
        }

        return buildFilm(urs);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }









    public Film create(Film film) {
        Integer generatedId = filmStorage.create(film);

        film.setId(generatedId);

        film.setMpa(mpaService.findMpa(film.getMpa().getId()));

        updateFilmGenres(generatedId, film.getGenres());
        return film;
    }


    public String like(int filmId, int userId) {
        checkExistence(filmId, userId);
        filmStorage.like(filmId, userId);
        return "Liked";
    }

    public String unlike(int filmId, int userId) {
        checkExistence(filmId, userId);
        filmStorage.unlike(filmId, userId);
        return "Unliked";
    }

    public List<Film> getPopular(int limitTo) {
        return buildFilmsList(filmStorage.getPopular(limitTo));
    }


    private void checkExistence(int filmId, int userId) {
        findFilm(filmId);

        if (!userStorage.doesUserExist(userId)) {
            throw new ElementNotFoundException("User " + userId + " not found");
        }
    }

    private Mpa getFilmMpaFromDb(int filmId) {
        SqlRowSet urs = filmStorage.getFilmMpaFromDB(filmId);
        if (!urs.next()) {
            throw new ElementNotFoundException(String.format("Film %d not found", filmId));
        }

        return Mpa.builder()
                .id(urs.getInt("MPA_ID"))
                .name(urs.getString("NAME"))
                .build();
    }

    private Film buildFilm(SqlRowSet urs) {
        int filmId = urs.getInt("film_id");
        return Film.builder()
                .id(filmId)
                .name(urs.getString("name"))
                .description(urs.getString("description"))
                .releaseDate(Objects.requireNonNull(urs.getDate("release_date")).toLocalDate())
                .duration(urs.getInt("duration"))
                .genres(genreStorage.getGenresOfFilmFromDB(filmId))
                .mpa(mpaStorage.getMpaFromDB(filmId))
                .build();
    }

    private List<Film> buildFilmsList(SqlRowSet urs) {
        List<Film> films = new ArrayList<>();

        while (urs.next()) {
            films.add(buildFilm(urs));
        }

        return films;
    }
}
