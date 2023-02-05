package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User findUser(Integer id);

    User create(User user);

    User update(User user);
    User delete(Integer id);

    User acceptFriend(int userId, int userToAddId);

    User addFriend(int userId, int userToAddId);

    List<User> findFriends(Integer id);

    User deleteFriend(int id, int friendId);
}
