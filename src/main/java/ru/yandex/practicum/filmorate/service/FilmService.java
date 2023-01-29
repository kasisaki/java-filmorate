package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void like(int filmId, int userId) {
        if (filmStorage.findFilm(filmId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
        }
        if (userStorage.findUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        filmStorage.findFilm(filmId).getLiked().add(userId);
        userStorage.findUser(userId).getLikedFilms().add(filmId);
    }

    public void unlike(int filmId, int userId) {
        if (filmStorage.findFilm(filmId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
        }
        if (userStorage.findUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        filmStorage.findFilm(filmId).getLiked().remove(userId);
        userStorage.findUser(userId).getLikedFilms().remove(filmId);
    }

    public List<Film> getTop(int limitTo) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> film.getLiked().size()))
                .limit(limitTo)
                .collect(Collectors.toList());
    }
}
