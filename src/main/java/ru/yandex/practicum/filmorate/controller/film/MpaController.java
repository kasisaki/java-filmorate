package ru.yandex.practicum.filmorate.controller.film;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.service.film.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;
    @GetMapping
    public List<Mpa> findAllMpa() {
        return mpaService.findAll(); //вернет список MPA или пустой лист
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mpa> getMpa(@PathVariable(required = false) int id) {
        return new ResponseEntity<>(mpaService.getMpa(id), HttpStatus.OK); //вернет нужный MPA или исключение
    }
}
