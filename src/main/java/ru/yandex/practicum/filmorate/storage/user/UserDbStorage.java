package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicateEmailException;
import ru.yandex.practicum.filmorate.exception.DuplicateLoginException;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, ((urs, rowNum) -> {
            return User.builder()
                    .id(urs.getInt("user_id"))
                    .login(urs.getString("login"))
                    .name(urs.getString("name"))
                    .email(urs.getString("email"))
                    .birthday(Objects.requireNonNull(urs.getDate("birthday")).toLocalDate())
                    .build();
        }));
    }

    @Override
    public User findUser(Integer id) {
        String sql = "SELECT * FROM users WHERE USER_ID = ?";
        SqlRowSet urs = jdbcTemplate.queryForRowSet(sql, id);

        if (!urs.next()) {
            throw new ElementNotFoundException(String.format("User %d not found", id));
        }
        User user = User.builder()
                .id(id)
                .login(urs.getString("login"))
                .name(urs.getString("name"))
                .email(urs.getString("email"))
                .birthday(Objects.requireNonNull(urs.getDate("birthday")).toLocalDate())
                .build();
        return user;
    }

    @Override
    public User create(User user) {
        if (checkDuplicateLogin(user.getLogin())) {
            throw new DuplicateLoginException(String.format("Failed to create user. User with login %s already exists",
                    user.getLogin()));
        }
        if (checkDuplicateEmail(user.getEmail())) {
            throw new DuplicateEmailException(String.format("Failed to create user. User with email %s already exists",
                    user.getEmail()));
        }
        String sql = "INSERT INTO users (login, name, email, birthday) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)",
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
        return null;
    }

    private boolean checkDuplicateLogin(String login) {
        String sql = "SELECT * FROM users WHERE login =?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, login);
        return userRows.next();
    }

    private boolean checkDuplicateEmail(String email) {
        String sql = "SELECT * FROM users WHERE email =?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, email);
        return userRows.next();
    }
}
