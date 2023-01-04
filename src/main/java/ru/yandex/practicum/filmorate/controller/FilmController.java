package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final HashMap<Long, Film> films = new HashMap<>();
    private final LocalDate dateLimit = LocalDate.of(1895, 12, 28);
    private long id = 0;

    @GetMapping
    public ArrayList<Film> findAll() {
        log.debug("Films count: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    @ResponseBody
    public Film create(@Valid @RequestBody Film film) {
        checkReleaseDate(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    @ResponseBody
    public Film update(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("No film found to update");
        }
    }

    private void checkReleaseDate(Film film) throws ValidationException {
        if (film.releaseDate.isBefore(dateLimit)) {
            throw new ValidationException("Release date should be after 28.12.1895");
        }
    }

    private long generateId() {
        return ++id;
    }
}
