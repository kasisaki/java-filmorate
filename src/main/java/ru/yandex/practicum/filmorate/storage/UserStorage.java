package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;

public interface UserStorage {

    ArrayList<User> findAll();

    ResponseEntity<User> findUser(String id);

    ResponseEntity<User> create(User user);

    ResponseEntity<User> update(User user);
}
