package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public ResponseEntity<User> findUser(int id) {
        User foundUser = userStorage.findUser(id);
        if (foundUser == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("No user found with id = %d", id));
        return new ResponseEntity<>(foundUser, HttpStatus.OK);
    }

    public ResponseEntity<User> create(User user) {
        return new ResponseEntity<>(userStorage.create(user), HttpStatus.OK);
    }

    public ResponseEntity<User> update(User user) {
        User updatedUser = userStorage.update(user);
        if (updatedUser == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("No user found to update with id = %d", user.getId()));
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    public ResponseEntity<User> addFriend(int userId, int userToAddId) {
        User user = userStorage.findUser(userId);
        User userToAdd = userStorage.findUser((userToAddId));
        if (user == null || userToAdd == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found and " +
                    "therefore cannot be added to friend list");
        } else if (userToAdd.getId() == user.getId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "We don't add ourselves to our friend list");
        } else if (userToAdd.getFriends().contains(user.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user is already in your friend list");
        }
        user.getFriends().add(userToAdd.getId());
        userToAdd.getFriends().add(user.getId());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<User> removeFriend(int id, int friendId) {
        User user = userStorage.findUser(id);
        User friend = userStorage.findUser(friendId);
        if (!user.getFriends().contains(friend.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User with id = %d is not in your" +
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        for (Integer friendsId : user.getFriends()) {
            if (friend.getFriends().contains(friendsId)) {
                commonFriends.add(userStorage.findUser(friendsId));
            }
        }
        return commonFriends;
    }
}
