package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Getter
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUser(@PathVariable(required = false) int id) { //string id changed to int
        return userService.findUser(id);
    }
    @GetMapping("{id}/friends")
    public ResponseEntity<List<User>> getAllFriends(@PathVariable int id) {
        return new ResponseEntity<>(userService.findAllFriends(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        return new ResponseEntity<>(userService.findCommonFriends(id, friendId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        return userService.removeFriend(id, friendId);
    }
}
