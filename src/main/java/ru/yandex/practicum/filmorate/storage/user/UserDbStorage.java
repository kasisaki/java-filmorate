package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.User;

import java.sql.PreparedStatement;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public SqlRowSet findAll() {
        String sql = "SELECT * FROM users";

        return  jdbcTemplate.queryForRowSet(sql);
    }

    @Override
    public SqlRowSet findUser(Integer id) {
        String sql = "SELECT * FROM users WHERE USER_ID = ?";

        return jdbcTemplate.queryForRowSet(sql, id);
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)";
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
        String sql = "UPDATE users SET login =?, name =?, email =?, birthday =? WHERE user_id =?";

        jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Integer delete(Integer id) {
        String sql = "DELETE FROM users WHERE user_id =?";

        return jdbcTemplate.update(sql, id);
    }

    @Override
    public SqlRowSet findFriends(Integer id) {
        String sql = String.format("SELECT * FROM users RIGHT JOIN ((SELECT TO_USER as t FROM FRIENDSHIPS WHERE FROM_USER = %d) " +
                "UNION (SELECT FROM_USER FROM FRIENDSHIPS WHERE TO_USER= %d AND ACCEPTED= true)) ON users.user_id = t", id, id);

        return jdbcTemplate.queryForRowSet(sql);
    }

    @Override
    public Integer addFriend(int userAddingId, int userToAddId) {
        String sql = "INSERT INTO friendships (from_user, to_user) VALUES (?,?)";

        return jdbcTemplate.update(sql, userAddingId, userToAddId);
    }


    @Override
    public Integer acceptFriend(int userAcceptingId, int userToAcceptId) {
        String sql = "UPDATE friendships SET accepted = true WHERE from_user =? AND to_user =?";

        return jdbcTemplate.update(sql, userToAcceptId, userAcceptingId);
    }

    @Override
    public Integer removeFriend(int userId, int userToDeleteId, String status) {
        String sql = "DELETE FROM friendships WHERE from_user =? AND to_user =?";

        return jdbcTemplate.update(sql + status, userId, userToDeleteId);
    }

    @Override
    public boolean doesUserExist(int user_id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE USER_ID = ?", user_id);

        return  userRows.next();
    }

    @Override
    public boolean doesFriendRequestExists(int fromUserId, int toUserId) {
        String sql = "SELECT * FROM friendships WHERE from_user =? AND to_user =?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, fromUserId, toUserId);

        return userRows.next();
    }
}
