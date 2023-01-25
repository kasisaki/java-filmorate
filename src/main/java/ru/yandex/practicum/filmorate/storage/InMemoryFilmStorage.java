package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {


    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 0;

    public ArrayList<Film> findAll() {
        log.debug("Films count: {}", films.size());
        return new ArrayList<>(films.values());
    }

    public ResponseEntity<Film> findFilm(String id) {
        Film film = films.get(Integer.parseInt(id));

        if (film == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    public ResponseEntity<Film> create(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    public ResponseEntity<Film> update(Film film) { //переделал без выкидывания исключений
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } else {
            log.debug("No film found to update");
            return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
        }
    }

    private int generateId() {
        return ++id;
    }


}
