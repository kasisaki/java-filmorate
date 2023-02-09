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
        return userService.findAll(); //вернет список пользователей или пустой
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable(required = false) int id) {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK); //вернет пользователя или исключение
    }

    @GetMapping("{id}/friends")
    public ResponseEntity<List<User>> getAllFriends(@PathVariable int id) {
        return new ResponseEntity<>(userService.getFriendsOfUser(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        return new ResponseEntity<>(userService.getCommonFriends(id, friendId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return new ResponseEntity<>(userService.create(user), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        return new ResponseEntity<>(userService.update(user), HttpStatus.OK);
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
