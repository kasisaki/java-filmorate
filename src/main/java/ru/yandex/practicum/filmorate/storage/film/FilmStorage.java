package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.film.Film;

import javax.validation.Valid;

public interface FilmStorage {

    SqlRowSet findAll();

    SqlRowSet findFilm(int id);

    Integer create(@Valid @RequestBody Film film);

    Film update(@Valid @RequestBody Film film);

    void like(int filmId, int userId);

    void unlike(int filmId, int userId);

    SqlRowSet getPopular(int limitTo);

    SqlRowSet getFilmMpaFromDB(int filmId);
}
