package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;

public interface FilmStorage {

    ArrayList<Film> findAll();

    Film findFilm(int id);

    Film create(@Valid @RequestBody Film film);

    Film update(@Valid @RequestBody Film film);
}
