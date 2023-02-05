package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.film.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmControllerTest {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    Set<ConstraintViolation<Film>> violations;

    @AfterEach
    void cleanup() {
        violations.clear();
    }

    @Test
    public void invalidName() {
        Film film = Film.builder().name("")

                .description("qweq")

                .duration(120)

                .releaseDate(LocalDate.of(1999, 12, 12))

                .build();

        violations = validator.validate(film);

        assertFalse(violations.isEmpty());

    }

    @Test
    public void invalidTooLongDescription() {
        Film film = Film.builder().name("QWerty 2023")

                .description("qwerty1234qwerty1234qwerty1234qwerty1234qwerty1234qwerty1234qwerty1234qwerty1234qwerty" +
                        "1234qwerty1234qwerty1234qwerty1234qwerty1234qwerty1234qwerty1234qwerty1234qwerty1234qwerty" +
                        "1234qwerty1234qwerty1234_extra")

                .duration(120)

                .releaseDate(LocalDate.of(1999, 12, 12))

                .build();

        violations = validator.validate(film);

        assertFalse(violations.isEmpty());

    }

    @Test
    public void invalidDurationZero() {
        Film film = Film.builder().name("Qwerty 2023")

                .description("qweq")

                .duration(0)

                .releaseDate(LocalDate.of(1999, 12, 12))

                .build();

        violations = validator.validate(film);

        assertFalse(violations.isEmpty());

    }

    @Test
    public void invalidDurationNegative() {
        Film film = Film.builder().name("Qwerty 2023")

                .description("qweq")

                .duration(0)

                .releaseDate(LocalDate.of(1999, 12, 12))

                .build();

        violations = validator.validate(film);

        assertFalse(violations.isEmpty());

    }

    @Test
    public void invalidReleaseDate() {
        Film film = Film.builder().name("Qwerty 2023")

                .description("qweq")

                .duration(100)

                .releaseDate(LocalDate.of(1895, 12, 17))

                .build();

        violations = validator.validate(film);

        assertFalse(violations.isEmpty());

    }

    @Test
    public void validReleaseDateFirstDay() {
        Film film = Film.builder().name("Qwerty 2023")

                .description("qweq")

                .duration(100)

                .releaseDate(LocalDate.of(1895, 12, 28))

                .build();

        violations = validator.validate(film);

        assertTrue(violations.isEmpty());

    }

    @Test
    public void validReleaseDateAnySomeInFuture() {
        Film film = Film.builder().name("Qwerty 2023")

                .description("qweq")

                .duration(100)

                .releaseDate(LocalDate.of(2895, 12, 28))

                .build();

        violations = validator.validate(film);

        assertTrue(violations.isEmpty());

    }
}
