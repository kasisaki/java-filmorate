package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreService genreService;
    private final MpaService mpaService;


    public List<Film> findAll() {
        return buildFilmsList(filmStorage.findAll());
    }

    public Film findFilm(int id) {
        SqlRowSet urs = filmStorage.findFilm(id);
        if (!urs.next()) {
            throw new ElementNotFoundException(String.format("Film %d not found", id));
        }

        return buildFilm(urs);
    }

    public Film update(Film film) {
        findFilm(film.getId());
        filmStorage.update(film);
        Mpa filmMpa = mpaService.findMpa(film.getMpa().getId());
        film.setMpa(filmMpa);

        setFilmGenres(film);
        return film;
    }

    public Film create(Film film) {
        film.setMpa(mpaService.findMpa(film.getMpa().getId()));
        filmStorage.create(film);
        setFilmGenres(film);

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

    private Film buildFilm(SqlRowSet urs) {
        int filmId = urs.getInt("film_id");
        return Film.builder()
                .id(filmId)
                .name(urs.getString("name"))
                .description(urs.getString("description"))
                .releaseDate(Objects.requireNonNull(urs.getDate("release_date")).toLocalDate())
                .duration(urs.getInt("duration"))
                .genres(genreService.getGenresOfFilm(filmId))
                .mpa(mpaService.findMpa(urs.getInt("MPA_ID")))
                .build();
    }

    private List<Film> buildFilmsList(SqlRowSet urs) {
        List<Film> films = new ArrayList<>();

        while (urs.next()) {
            films.add(buildFilm(urs));
        }

        return films;
    }

    private void setFilmGenres(Film film) {
        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genres.add(genreService.findGenre(genre.getId()));
            }
            film.setGenres(genres);
        }
    }
}
