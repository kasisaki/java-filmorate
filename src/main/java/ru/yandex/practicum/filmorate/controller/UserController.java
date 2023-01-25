package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping("/users")
@Slf4j
@Getter
public class UserController {

    private final UserStorage userStorage;

    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public ArrayList<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUser(@PathVariable(required = false) String id) {
        return userStorage.findUser(id);
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }
}
