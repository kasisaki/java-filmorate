package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exception.dbException.FriendshipExistsException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {

        List<User> users = new ArrayList<>();
        SqlRowSet urs = userStorage.findAll();

        while (urs.next()) {
            users.add(buildUser(urs));
        }

        return users;
    }

    public User findUser(int id) {
        SqlRowSet urs = userStorage.findUser(id);

        if (urs.next()) {
            return buildUser(urs);
        }
        throw new ElementNotFoundException("User with id " + id + " not found");
    }

    public ResponseEntity<User> create(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
            log.warn("Name is not provided and set to match login");
        }
        return new ResponseEntity<>(userStorage.create(user), HttpStatus.OK);
    }

    public ResponseEntity<User> update(User user) {
        if (!userStorage.doesUserExist(user.getId())) {
            throw new ElementNotFoundException(String.format("User %d not found", user.getId()));
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
            log.warn("Name is not provided and set to match login");
        }

        User updatedUser = userStorage.update(user);
        if (updatedUser == null) throw new ElementNotFoundException(String.format("User %d not found", user.getId()));
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    public String addFriend(int userAddingId, int userToAddId) {
        if (userAddingId == userToAddId) {
            throw new BadRequestException("We don't add ourselves to our friend list");
        }
        if (!userStorage.doesUserExist(userAddingId)) {
            throw new ElementNotFoundException(String.format("User %d not found", userAddingId));
        }

        if (!userStorage.doesUserExist(userToAddId)) {
            throw new ElementNotFoundException(String.format("User %d not found", userToAddId));
        }

        if (userStorage.doesFriendRequestExists(userAddingId, userToAddId)) {
            throw new FriendshipExistsException(String.format("User %d is already your friend", userToAddId));
        }

        if (userStorage.doesFriendRequestExists(userToAddId, userAddingId)) {
            return "Updated " + userStorage.acceptFriend(userAddingId, userToAddId) + "entries";
        }
        return "Updated " + userStorage.addFriend(userAddingId, userToAddId) + "entries";
    }

    public String acceptFriend(int userId, int userToAddId) {
        return "Updated " + userStorage.acceptFriend(userId, userToAddId) + "entries";
    }

    public String removeFriend(int userId, int friendId) {
        if (userStorage.doesFriendRequestExists(userId, friendId)) {
            return "Updated " + userStorage.removeFriend(userId, friendId, "") + " entries";
        }
        if (userStorage.doesFriendRequestExists(friendId, userId)) {
            return "Updated " + userStorage.removeFriend(friendId, userId, " AND accepted = true") + " entries";
        }
        throw new ElementNotFoundException(String.format("User %d is not in your friendList", friendId));
    }

    public List<User> findFriends(int id) {
        List<User> users = new ArrayList<>();
        SqlRowSet urs = userStorage.findFriends(id);

        while (urs.next()) {
            users.add(buildUser(urs));
        }

        return users;
    }

    public List<User> findCommonFriends(int userId, int friendId) {
        return findFriends(userId).stream().filter(u ->
                findFriends(friendId).contains(u)).collect(Collectors.toList());
    }

    public String delete(int id) {
        return "Deleted " + userStorage.delete(id) + " entries";
    }

    private User buildUser(SqlRowSet urs) {
        return User.builder()
                .id(urs.getInt("user_id"))
                .login(urs.getString("login"))
                .name(urs.getString("name"))
                .email(urs.getString("email"))
                .birthday(Objects.requireNonNull(urs.getDate("birthday")).toLocalDate())
                .build();
    }
}
