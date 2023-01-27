package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;

public interface FilmStorage {

    ArrayList<Film> findAll();

    ResponseEntity<Film> findFilm(String id);

    ResponseEntity<?> create(@Valid @RequestBody Film film);

    ResponseEntity<?> update(@Valid @RequestBody Film film);
}
