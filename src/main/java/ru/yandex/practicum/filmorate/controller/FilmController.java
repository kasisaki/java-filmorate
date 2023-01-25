package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

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
    public ResponseEntity<Film> findFilm(@PathVariable(required = false) String id) {
        return filmStorage.findFilm(id);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<?> update(@Valid @RequestBody Film film) { //переделал без выкидывания исключений
        return filmStorage.update(film);
    }
}
