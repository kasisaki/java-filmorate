package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    public List<User> findAll() {
        log.debug("Users count: {}", users.size());
        return new ArrayList<>(users.values());
    }

    public User findUser(Integer id) {
        return users.get(id);
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
            log.warn("Name is not provided and set to match login");
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.debug("User with login \"{}\" added.", user.getLogin());
        return user;
    }

    public User update(User user) {
        User foundUser = findUser(user.getId());
        if (foundUser == null) {
            log.warn("No user with id " + user.getId());
            return null;
        }
        foundUser.setLogin(user.getLogin());
        foundUser.setName(user.getName());
        if (foundUser.getName() == null || foundUser.getName().equals("")) {
            foundUser.setName(foundUser.getLogin());
        }
        foundUser.setBirthday(user.getBirthday());
        foundUser.setEmail(user.getEmail());
        log.debug("User with id \"{}\" updated.", user.getId());
        return user;
    }

    private int generateId() {
        return ++id;
    }


}
