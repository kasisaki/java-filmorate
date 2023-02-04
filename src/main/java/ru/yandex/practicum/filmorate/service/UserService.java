package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public ResponseEntity<User> findUser(int id) {
        return new ResponseEntity<>(userStorage.findUser(id), HttpStatus.OK);
    }

    public ResponseEntity<User> create(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
            log.warn("Name is not provided and set to match login");
        }
        return new ResponseEntity<>(userStorage.create(user), HttpStatus.OK);
    }

    public ResponseEntity<User> update(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
            log.warn("Name is not provided and set to match login");
        }
        User updatedUser = userStorage.update(user);
        if (updatedUser == null) throw new ElementNotFoundException(String.format("User %d not found", user.getId()));
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    public ResponseEntity<User> addFriend(int userId, int userToAddId) {
        if (userId == userToAddId) {
            throw new BadRequestException("We don't add ourselves to our friend list");
        }
        return new ResponseEntity<>(userStorage.addFriend(userId, userToAddId), HttpStatus.OK);
    }

    public ResponseEntity<User> acceptFriend(int userId, int userToAddId) {
        return new ResponseEntity<>(userStorage.acceptFriend(userId, userToAddId), HttpStatus.OK);
    }

    public ResponseEntity<User> removeFriend(int id, int friendId) {
        return new ResponseEntity<>(userStorage.deleteFriend(id, friendId), HttpStatus.OK);
    }

    public List<User> findAllFriends(int id) {
        return userStorage.findFriends(id);
    }

    public List<User> findCommonFriends(int userId, int friendId) {
        return userStorage.findFriends(userId).stream().filter(u ->
                userStorage.findFriends(friendId).contains(u)).collect(Collectors.toList());
    }

    public ResponseEntity<User> delete(int id) {
        return new ResponseEntity<>(userStorage.delete(id), HttpStatus.OK);
    }
}
