package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    public ArrayList<User> findAll() {
        log.debug("Users count: {}", users.size());
        return new ArrayList<>(users.values());
    }

    public ResponseEntity<User> findUser(String id) {
        User user = users.get(Integer.parseInt(id));

        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<User> create(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
            log.warn("Name is not provided and set to match login");
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.debug("User with login \"{}\" added.", user.getLogin());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<User> update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("User with login \"{}\" updated.", user.getLogin());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            log.debug("No user with id " + user.getId());
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }
    }

    private int generateId() {
        return ++id;
    }


}
