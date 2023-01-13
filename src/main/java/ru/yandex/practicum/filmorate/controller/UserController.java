package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@RestController
@RequestMapping("/users")
@Slf4j
@Getter
public class UserController {
    private final HashMap<Long, User> users = new HashMap<>();
    private long id = 0;

    @GetMapping
    public ArrayList<User> findAll() {
        log.debug("Users count: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> create(@Valid @RequestBody User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
            System.out.println("Name is not provided and set to match login");
            log.warn("Name is not provided and set to match login");
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.debug("User with login \"{}\" added.", user.getLogin());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<?> update(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("User with login \"{}\" updated.", user.getLogin());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            log.debug("No user with id " + user.getId());
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }
    }

    private long generateId() {
        return ++id;
    }
}
