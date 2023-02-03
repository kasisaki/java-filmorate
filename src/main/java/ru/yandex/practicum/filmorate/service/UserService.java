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
        User user = userStorage.findUser(userId);
        User userToAdd = userStorage.findUser((userToAddId));
        if (user == null || userToAdd == null) {
            throw new ElementNotFoundException("User " + userId + " not found and" +
                    " therefore cannot be added to friend list");
        } else if (userToAdd.getId() == user.getId()) {
            throw new BadRequestException("We don't add ourselves to our friend list");
        } else if (userToAdd.getFriends().contains(user.getId())) {
            throw new BadRequestException("User " + userToAddId + " is already in your friend list");
        }
        user.getFriends().add(userToAdd.getId());
        userToAdd.getFriends().add(user.getId());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<User> removeFriend(int id, int friendId) {
        User user = userStorage.findUser(id);
        User friend = userStorage.findUser(friendId);
        if (!user.getFriends().contains(friend.getId())) {
            throw new BadRequestException(String.format("User %d is not in your" +
                    " friend list", friend.getId()));
        }
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public List<User> findAllFriends(int id) {
        List<User> listOfFriends = new ArrayList<>();
        for (int friendId : userStorage.findUser(id).getFriends()) {
            listOfFriends.add(userStorage.findUser(friendId));
        }
        return listOfFriends;
    }

    public List<User> findCommonFriends(int userId, int friendId) {
        ArrayList<User> commonFriends = new ArrayList<>();
        User friend = userStorage.findUser(friendId);
        User user = userStorage.findUser(userId);
        if (user == null) {
            throw new ElementNotFoundException("User " + userId + " not found");
        }
        if (friend == null) {
            throw new ElementNotFoundException("User " + friendId + " not found");
        }
        for (Integer friendsId : user.getFriends()) {
            if (friend.getFriends().contains(friendsId)) {
                commonFriends.add(userStorage.findUser(friendsId));
            }
        }
        return commonFriends;
    }
}
