package ru.yandex.practicum.filmorate.model.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserConstrainViolationTest {
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
