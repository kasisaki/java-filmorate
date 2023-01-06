package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

import static ru.yandex.practicum.filmorate.util.Constants.DATE_LIMIT;

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
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        checkReleaseDate(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    @ResponseBody
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("No film found to update");
        }
    }

    private void checkReleaseDate(Film film) throws ValidationException {
        if (film.releaseDate.isBefore(DATE_LIMIT)) {
            throw new ValidationException("Release date should be after 28.12.1895");
        }
    }

    private long generateId() {
        return ++id;
    }
}
