package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.user.User;

public interface UserStorage {

    SqlRowSet findAll();

    SqlRowSet findUser(Integer userId);

    User create(User user);

    User update(User user);
    Integer delete(Integer userId);

    Integer acceptFriend(int userId, int userToAddId);

    Integer addFriend(int userId, int userToAddId);

    SqlRowSet findFriends(Integer userId);

    Integer removeFriend(int userId, int friendId, String status);

    boolean doesUserExist(int userAddingId);

    boolean doesFriendRequestExists(int userAddingId, int userToAddId);
}
