package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

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
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUser(@PathVariable(required = false) String id) {
        User foundUser = userStorage.findUser(Integer.parseInt(id));
        if (foundUser == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("No user found with id = %s", id));
        return new ResponseEntity<>(foundUser, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return new ResponseEntity<>(userStorage.create(user), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        User updatedUser = userStorage.update(user);
        if (updatedUser == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("No user found to update with id = %d", user.getId()));
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}
