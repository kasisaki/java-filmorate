package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilm(int id) {
        Film foundFilm = filmStorage.findFilm(id);
        if (foundFilm == null) throw new ElementNotFoundException("Film " + id + " not found");
        return foundFilm;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void like(int filmId, int userId) {
        checkExistence(filmId, userId);
        filmStorage.like(filmId, userId);
    }

    public void unlike(int filmId, int userId) {
        checkExistence(filmId, userId);
        filmStorage.unlike(filmId, userId);
    }

    public List<Film> getPopular(int limitTo) {
        return filmStorage.getPopular(limitTo);
    }


    private void checkExistence(int filmId, int userId) {
        Film foundFilm = filmStorage.findFilm(filmId);

        if (foundFilm == null) {
            throw new ElementNotFoundException("Film " + filmId + " not found");
        }
        if (!userStorage.findUser(userId).next()) {
            throw new ElementNotFoundException("User " + userId + " not found");
        }
    }
}
