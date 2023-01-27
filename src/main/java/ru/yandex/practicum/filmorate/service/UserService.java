package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public ResponseEntity<User> addFriend(User UserToAdd, User user) {
        if (userStorage.findUser(user.getId()) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with id %d is not found and " +
                    "therefore cannot be added to friend list", user.getId()));
        } else if (UserToAdd.getId() == user.getId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "We don't add ourselves to our friend list");
        } else if (UserToAdd.getFriends().contains(user.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user is already in your friend list");
        }
        user.getFriends().add(UserToAdd.getId());
        UserToAdd.getFriends().add(user.getId());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<User> removeFriend(User userToRemove, User user) {
        if (!user.getFriends().contains(userToRemove.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User with id = %d is not in your" +
                    " friend list", userToRemove.getId()));
        }
        user.getFriends().remove(userToRemove.getId());
        userToRemove.getFriends().remove(user.getId());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public List<User> findCommonFriends(User userToCheckWith, User user) {
        ArrayList<User> commonFriends = new ArrayList<>();
        for (Integer friendsId : user.getFriends()) {
            if (userToCheckWith.getFriends().contains(friendsId)) {
                commonFriends.add(userStorage.findUser(friendsId));
            }
        }
        return commonFriends;
    }
}
