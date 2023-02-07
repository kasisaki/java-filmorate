package ru.yandex.practicum.filmorate.controller.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Getter
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUser(@PathVariable(required = false) int id) { //string id changed to int
        return new ResponseEntity<>(userService.findUser(id), HttpStatus.OK);
    }

    @GetMapping("{id}/friends")
    public ResponseEntity<List<User>> getAllFriends(@PathVariable int id) {
        return new ResponseEntity<>(userService.findFriends(id), HttpStatus.OK);
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
    public ResponseEntity<String> addFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        return new ResponseEntity<>(userService.addFriend(id, friendId), HttpStatus.OK);
    }

    @PutMapping("/{id}/friends/{friendId}/status")
    public ResponseEntity<String> acceptFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        return new ResponseEntity<>(userService.acceptFriend(id, friendId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") int id) {
        return new ResponseEntity<>(userService.delete(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        return new ResponseEntity<>(userService.removeFriend(id, friendId), HttpStatus.OK);
    }
}
