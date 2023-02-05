package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exception.dbException.DuplicateEmailException;
import ru.yandex.practicum.filmorate.exception.dbException.DuplicateLoginException;
import ru.yandex.practicum.filmorate.exception.dbException.FriendshipExistsException;
import ru.yandex.practicum.filmorate.model.user.User;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, ((urs, rowNum) -> User.builder()
                .id(urs.getInt("user_id"))
                .login(urs.getString("login"))
                .name(urs.getString("name"))
                .email(urs.getString("email"))
                .birthday(Objects.requireNonNull(urs.getDate("birthday")).toLocalDate())
                .build()));
    }

    @Override
    public User findUser(Integer id) {
        String sql = "SELECT * FROM users WHERE USER_ID = ?";
        SqlRowSet urs = jdbcTemplate.queryForRowSet(sql, id);

        if (!urs.next()) {
            throw new ElementNotFoundException(String.format("User %d not found", id));
        }
        return User.builder()
                .id(id)
                .login(urs.getString("login"))
                .name(urs.getString("name"))
                .email(urs.getString("email"))
                .birthday(Objects.requireNonNull(urs.getDate("birthday")).toLocalDate())
                .build();
    }

    @Override
    public User create(User user) {
        throwExceptionOnDuplicateLogin(user.getLogin(), 0);
        throwExceptionOnDuplicateEmail(user.getEmail(), 0);

        String sql = "INSERT INTO users (login, name, email, birthday) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                    new String[]{"user_id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        int generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        user.setId(generatedId);
        return user;
    }


    @Override
    public User update(User user) {
        String sql = "SELECT * FROM users WHERE USER_ID = ?";
        SqlRowSet urs = jdbcTemplate.queryForRowSet(sql, user.getId());
        if (!urs.next()) {
            throw new ElementNotFoundException(String.format("User %d not found", user.getId()));
        }
        throwExceptionOnDuplicateLogin(user.getLogin(), user.getId());
        throwExceptionOnDuplicateEmail(user.getEmail(), user.getId());
        sql = "UPDATE users SET login =?, name =?, email =?, birthday =? WHERE user_id =?";
        jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public User delete(Integer id) {
        User user = findUser(id);
        if (user == null) {
            throw new ElementNotFoundException(String.format("User %d not found", id));
        }
        String sql = "DELETE FROM users WHERE user_id =?";
        jdbcTemplate.update(sql, id);
        return user;
    }

    @Override
    public List<User> findFriends(Integer id) {
        String sqlFrom = "SELECT TO_USER FROM friendships WHERE FROM_USER =" + id;
        String sqlTo = "SELECT FROM_USER FROM friendships WHERE TO_USER =" + id + " AND accepted = true";
        List<Integer> idsFrom = jdbcTemplate.queryForList(sqlFrom, Integer.class);
        List<Integer> idsTo = jdbcTemplate.queryForList(sqlTo, Integer.class);
        List<Integer> total = Stream.concat(idsFrom.stream(), idsTo.stream()).distinct().collect(Collectors.toList());
        List<User> users = new ArrayList<>();
        for (Integer i : total) {
            users.add(findUser(i));
        }
        return users;
    }

    @Override
    public User addFriend(int userAddingId, int userToAddId) {
        userExistsOrThrow(userAddingId);
        userExistsOrThrow(userToAddId);
        if (checkFriendRequestExists(userToAddId, userAddingId)) {
            return acceptFriend(userAddingId, userToAddId);
        }
        if (checkFriendRequestExists(userAddingId, userToAddId)) {
            throw new FriendshipExistsException(String.format("User %d is already your friend", userToAddId));
        }

        String sql = "INSERT INTO friendships (from_user, to_user) VALUES (?,?)";
        jdbcTemplate.update(sql, userAddingId, userToAddId);

        return findUser(userToAddId);
    }


    @Override
    public User acceptFriend(int userAcceptingId, int userToAcceptId) {
        userExistsOrThrow(userAcceptingId);
        userExistsOrThrow(userToAcceptId);

        String sql = "UPDATE friendships SET accepted = true WHERE from_user =? AND to_user =?";
        jdbcTemplate.update(sql, userToAcceptId, userAcceptingId);
        return findUser(userToAcceptId);
    }

    @Override
    public User deleteFriend(int userId, int userToDeleteId) {
        String sql = "DELETE FROM friendships WHERE from_user =? AND to_user =?";
        if (checkFriendRequestExists(userId, userToDeleteId)) {
            jdbcTemplate.update(sql, userId, userToDeleteId);
            return findUser(userToDeleteId);
        }
        sql = "DELETE FROM friendships WHERE from_user =? AND to_user =? AND accepted = true";
        if (checkFriendRequestExists(userToDeleteId, userId)) {
            jdbcTemplate.update(sql, userToDeleteId, userId);
            return findUser(userToDeleteId);
        }
        throw new ElementNotFoundException(String.format("User %d is not in your friendList", userToDeleteId));
    }


    private void throwExceptionOnDuplicateLogin(String login, int id) {
        String sql = "SELECT * FROM users WHERE login =? AND user_id!=?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, login, id);
        if (userRows.next()) {
            throw new DuplicateLoginException(String.format("Failed to create user. User with login %s already exists",
                    login));
        }
    }

    private void throwExceptionOnDuplicateEmail(String email, int id) {
        String sql = "SELECT * FROM users WHERE email =? AND user_id =?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, email, id);
        if (userRows.next()) {
            throw new DuplicateEmailException(String.format("Failed to create user. User with email %s already exists",
                    email));
        }
    }

    private void userExistsOrThrow(int id) {
        String sql = "SELECT * FROM users WHERE user_id =?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!userRows.next()) {
            throw new ElementNotFoundException(String.format("User %d not found", id));
        }
    }

    private boolean checkFriendRequestExists(int fromUserId, int toUserId) {
        String sql = "SELECT * FROM friendships WHERE from_user =? AND to_user =?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, fromUserId, toUserId);
        return userRows.next();
    }
}
