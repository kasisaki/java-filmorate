package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

import static ru.yandex.practicum.filmorate.util.Constants.FILM_COUNT_DEFAULT;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> findFilm(@PathVariable(required = false) int id) {
        return filmService.findFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(required = false) Integer count) {
        if (count == null || count <= 0) {
            return filmService.getPopular(FILM_COUNT_DEFAULT);
        }
        return filmService.getPopular(count);
    }

    @PostMapping
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> likeFilm(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        return new ResponseEntity<>(filmService.like(filmId, userId), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Film> update(@Valid @RequestBody Film film) { //переделал без выкидывания исключений
        return filmService.update(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> unlike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        return new ResponseEntity<>(filmService.unlike(filmId, userId), HttpStatus.OK);
    }
}
