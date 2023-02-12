package ru.yandex.practicum.filmorate.controller.film;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.service.film.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<Genre> findAllGenres() {
        return genreService.findAllGenres(); //вернет или список жанров или пустой лист
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenre(@PathVariable(required = false) int id) {
        return new ResponseEntity<>(genreService.getGenre(id), HttpStatus.OK); //вернет нужный жанр или исключение
    }
}
