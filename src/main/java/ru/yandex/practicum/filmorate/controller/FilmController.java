package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public ArrayList<Film> findAll() {
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> findFilm(@PathVariable(required = false) int id) {
        Film foundFilm = filmStorage.findFilm(id);
        if (foundFilm == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
        return new ResponseEntity<>(foundFilm, HttpStatus.OK);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        return new ResponseEntity<>(filmStorage.create(film), HttpStatus.CREATED);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Film> update(@Valid @RequestBody Film film) { //переделал без выкидывания исключений
        Film updatedFilm = filmStorage.update(film);
        if (updatedFilm == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No film found to update");
        return new ResponseEntity<>(updatedFilm, HttpStatus.OK);
    }
}
