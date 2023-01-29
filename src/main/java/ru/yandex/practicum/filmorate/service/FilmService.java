package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public ResponseEntity<Film> findFilm(int id) {
        Film foundFilm = filmStorage.findFilm(id);
        if (foundFilm == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
        return new ResponseEntity<>(foundFilm, HttpStatus.OK);
    }

    public ResponseEntity<Film> create(Film film) {
        return new ResponseEntity<>(filmStorage.create(film), HttpStatus.CREATED);
    }

    public ResponseEntity<Film> update(Film film) {
        Film updatedFilm = filmStorage.update(film);
        if (updatedFilm == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No film found to update");
        return new ResponseEntity<>(updatedFilm, HttpStatus.OK);
    }

    public Film like(int filmId, int userId) {
        Film foundFilm = filmStorage.findFilm(filmId);
        User user = userStorage.findUser(userId);

        if (foundFilm == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
        }
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        foundFilm.getLikesFromUsers().add(userId);
        user.getLikedFilms().add(filmId);
        return foundFilm;
    }

    public Film unlike(int filmId, int userId) {
        Film foundFilm = filmStorage.findFilm(filmId);
        User user = userStorage.findUser(userId);

        if (foundFilm == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
        }
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        foundFilm.getLikesFromUsers().remove(userId);
        user.getLikedFilms().remove(filmId);
        return foundFilm;
    }

    public List<Film> getPopular(int limitTo) {
        return filmStorage.findAll().stream()
                .sorted((film1, film2) -> film2.getLikesFromUsers().size() - film1.getLikesFromUsers().size())
                .limit(limitTo)
                .collect(Collectors.toList());
    }
}
