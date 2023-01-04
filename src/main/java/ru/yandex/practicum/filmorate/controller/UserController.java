package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@Slf4j
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
    public User create(@Valid @RequestBody User user) {
        validateLogin(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.debug("User with login {} added.", user.getLogin());
        return user;
    }

    @PutMapping
    @ResponseBody
    public User update(@Valid @RequestBody User user) throws ValidationException {
        validateLogin(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("User with login {} updated.", user.getLogin());
            return user;
        } else {
            throw new ValidationException("No user with id " + user.getId());
        }
    }

    private void validateLogin(User user) {
        if (user.getLogin().matches("\\s+")) {
            throw new javax.validation.ValidationException("Login must not contain spaces");
        }
    }

    private long generateId() {
        return ++id;
    }
}
