package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final HashMap<Long, Film> films = new HashMap<>();
    private long id = 0;

    @GetMapping
    public ArrayList<Film> findAll() {
        log.debug("Films count: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> create(@Valid @RequestBody Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<?> update(@Valid @RequestBody Film film) { //переделал без выкидывания исключений
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } else {
            log.debug("No film found to update");
            return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
        }
    }

    private long generateId() {
        return ++id;
    }
}
