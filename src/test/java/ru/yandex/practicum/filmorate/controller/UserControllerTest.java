package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.user.UserController;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    Set<ConstraintViolation<User>> violations;

    @AfterEach
    void cleanup() {
        violations.clear();
    }

    @Test
    public void validUser() {
        User user = User.builder().login("loginnospace")

                .name("TestUserName")

                .email("test@yandex.ru")

                .birthday(LocalDate.of(1999, 12, 12))

                .build();

        violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void validUserNoName() {
        User user = User.builder().login("loginnospace")

                .email("test@yandex.ru")

                .birthday(LocalDate.of(1999, 12, 12))

                .build();

        UserController userController = new UserController(new UserService(new UserDbStorage(new JdbcTemplate())));

        userController.create(user);

        violations = validator.validate(user);

        assertEquals(userController.findAll().get(0).getName(), user.getLogin());
    }

    @Test
    public void invalidLoginEmpty() {
        User user = User.builder().login("")

                .name("TestUserName")

                .email("test@yandex.ru")

                .birthday(LocalDate.of(1999, 12, 12))

                .build();

        violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void invalidLoginWithSpace() {
        User user = User.builder().login("login space")

                .name("TestUserName")

                .email("test@yandex.ru")

                .birthday(LocalDate.of(1999, 12, 12))

                .build();

        violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void invalidBirthdayInFuture() {
        User user = User.builder().login("loginnospace")

                .name("TestUserName")

                .email("test@yandex.ru")

                .birthday(LocalDate.of(2999, 12, 12))

                .build();

        violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void invalidEmail() {
        User user = User.builder().login("loginnospace")

                .name("TestUserName")

                .email("testyandex.ru@")

                .birthday(LocalDate.of(1999, 12, 12))

                .build();

        violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void invalidEmail2() {
        User user = User.builder().login("loginnospace")

                .name("TestUserName")

                .email("testyandex.ru")

                .birthday(LocalDate.of(1999, 12, 12))

                .build();

        violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void invalidEmail3() {
        User user = User.builder().login("loginnospace")

                .name("TestUserName")

                .email("@@@")

                .birthday(LocalDate.of(1999, 12, 12))

                .build();

        violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }


}
