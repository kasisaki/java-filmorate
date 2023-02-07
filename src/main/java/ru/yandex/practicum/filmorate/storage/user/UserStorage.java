package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.user.User;

public interface UserStorage {

    SqlRowSet findAll();

    SqlRowSet findUser(Integer id);

    User create(User user);

    User update(User user);
    Integer delete(Integer id);

    Integer acceptFriend(int userId, int userToAddId);

    Integer addFriend(int userId, int userToAddId);

    SqlRowSet findFriends(Integer id);

    Integer removeFriend(int id, int friendId, String status);

    boolean doesUserExist(int userAddingId);

    boolean doesFriendRequestExists(int userAddingId, int userToAddId);
}
