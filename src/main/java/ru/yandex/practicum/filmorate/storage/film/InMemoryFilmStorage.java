package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryFilmStorage {


    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 0;

    public List<Film> findAll() {
        log.debug("Films count: {}", films.size());
        return new ArrayList<>(films.values());
    }

    public Film findFilm(int id) {
        return films.get(id);
    }

    public Film create(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) {
        int filmId = film.getId();
        if (films.containsKey(filmId)) {
            films.put(filmId, film);
            return film;
        } else {
            log.debug("No film found to update");
            return null;
        }
    }

    private int generateId() {
        return ++id;
    }


}
