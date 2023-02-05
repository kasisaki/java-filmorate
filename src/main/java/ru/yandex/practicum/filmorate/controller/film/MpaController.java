package ru.yandex.practicum.filmorate.controller.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.film.MpaService;

import javax.validation.Valid;
import java.util.List;

import static ru.yandex.practicum.filmorate.util.Constants.FILM_COUNT_DEFAULT;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;
    @GetMapping
    public List<Mpa> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mpa> findGenre(@PathVariable(required = false) int id) {
        return new ResponseEntity<>(mpaService.findMpa(id), HttpStatus.OK);
    }
}
