package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.controller.film.FilmController;
import ru.yandex.practicum.filmorate.controller.film.GenreController;
import ru.yandex.practicum.filmorate.controller.film.MpaController;
import ru.yandex.practicum.filmorate.controller.user.UserController;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(scripts = {"/schema.sql"},config = @SqlConfig(encoding = "UTF-8")),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/test-data.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/drop-test-data.sql"),
})
class FilmorateApplicationTests {

    private final FilmController filmController;
    private final UserController userController;
    private final GenreController genreController;
    private final MpaController mpaController;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void testTheDbIsEmpty() {
        assertFalse(jdbcTemplate.queryForRowSet("SELECT * FROM USERS").next());
        assertFalse(jdbcTemplate.queryForRowSet("SELECT * FROM FILMS").next());
    }

    @Test
    public void contextLoads() {
        assertTrue(true);
    }

    @Test
    public void testGetListOfGenres() {
        List<Genre> genres = genreController.findAllGenres();

        assertEquals(1, genres.get(0).getId());
        assertEquals(6, genres.get(5).getId());
        assertEquals("????????????????????", genres.get(2).getName());
    }

    @Test
    public void testAddUser() {
        ResponseEntity<User> responseUser = userController.createUser(
                User.builder()
                        .login("FirstTestUser")
                        .email("first@email.com")
                        .birthday(LocalDate.of(1900, 1, 1))
                        .build());

        assertEquals(responseUser.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(responseUser.getBody()).getLogin(), "FirstTestUser");
        assertEquals(responseUser.getBody().getName(), "FirstTestUser");
    }

    @Test
    public void testGetUserById() {
        testAddUser();
        ResponseEntity<User> responseUser = userController.getUser(1);

        assertEquals(responseUser.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(responseUser.getBody()).getName(), "FirstTestUser");
        assertEquals(responseUser.getBody().getEmail(), "first@email.com");
    }

    @Test
    public void testFindAllUsers() {
        testAddUser();
        userController.createUser(
                User.builder()
                        .login("SecondTestUser")
                        .email("second@email.com")
                        .birthday(LocalDate.of(1900, 1, 1))
                        .build());

        List<User> users = userController.findAllUsers();

        assertEquals(users.get(0).getId(), 1);
        assertEquals(users.get(0).getLogin(), "FirstTestUser");

        assertEquals(users.get(1).getId(), 2);
        assertEquals(users.get(1).getLogin(), "SecondTestUser");
    }

    @Test
    public void testGetUnknownUser() {
        try {
            ResponseEntity<User> userUnknown = userController.getUser(999);
            assertEquals(userUnknown.getStatusCode(), HttpStatus.NOT_FOUND);
        } catch (ElementNotFoundException e) {
            assertEquals(e.getClass().getName(), ElementNotFoundException.class.getName());
        }
    }

    @Test
    public void testAddFriend() {
        testFindAllUsers();
        ResponseEntity<String> addFriendResponse = userController.addFriend(1, 2);

        assertEquals(addFriendResponse.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testGetFriends() {
        testAddFriend();
        ResponseEntity<List<User>> friendUser = userController.getAllFriends(1);

        assertEquals(friendUser.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(friendUser.getBody()).get(0).getId(), 2);
    }

    @Test
    public void testGetCommonFriends() {
        testAddFriend();
        userController.createUser(
                User.builder()
                        .login("thirdTestUser")
                        .email("third@email.com")
                        .birthday(LocalDate.of(1900, 1, 1))
                        .build());
        userController.addFriend(1, 3);
        userController.addFriend(2, 3);
        ResponseEntity<List<User>> commonList = userController.getCommonFriends(1, 2);

        assertEquals(commonList.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(commonList.getBody()).get(0).getId(), 3);
        assertTrue(commonList.getBody().get(0).getName().contains("third"));
    }

    @Test
    public void testAddFilms() {
        Film film1 = Film.builder()
                .name("firstFilmName")
                .duration(100)
                .description("Description of first film")
                .releaseDate(LocalDate.of(2023, 2, 10))
                .mpa(Mpa.builder().id(1).build())
                .genres(Set.of(Genre.builder().id(1).build()))
                .build();

        Film film2 = Film.builder()
                .name("secondFilmName")
                .duration(200)
                .mpa(Mpa.builder().id(2).build())
                .genres(Set.of(Genre.builder().id(2).build()))
                .description("Description of second film")
                .releaseDate(LocalDate.of(2023, 2, 10))
                .build();

        ResponseEntity<Film> firstFilmEntity = filmController.create(film1);
        ResponseEntity<Film> secondFilmEntity = filmController.create(film2);
        assertEquals(firstFilmEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(secondFilmEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(Objects.requireNonNull(firstFilmEntity.getBody()).getId(), 1);
        assertEquals(Objects.requireNonNull(secondFilmEntity.getBody()).getId(), 2);
    }

    @Test
    public void testFindAllFilm() {
        testAddFilms();
        List<Film> films = filmController.findAll();

        assertEquals(films.get(0).getId(), 1);
        assertFalse(films.get(0).getGenres().isEmpty());
        assertEquals(films.get(0).getMpa().getId(), 1);
        assertTrue(films.get(0).getName().contains("first"));

        assertEquals(films.get(1).getId(), 2);
        assertFalse(films.get(1).getGenres().isEmpty());
        assertEquals(films.get(1).getMpa().getId(), 2);
        assertTrue(films.get(1).getName().contains("second"));
    }

    @Test
    public void testLike() {
        testAddFilms();
        testGetCommonFriends();
        ResponseEntity<String> firstLike = filmController.likeFilm(2, 3);
        ResponseEntity<String> secondLike = filmController.likeFilm(2, 2);

        assertEquals(firstLike.getStatusCode(), HttpStatus.OK);
        assertEquals(secondLike.getStatusCode(), HttpStatus.OK);

        assertEquals(firstLike.getBody(), "Liked");
        assertEquals(secondLike.getBody(), "Liked");
    }

    @Test
    public void testFindPopular() {
        testLike();
        List<Film> popular = filmController.findPopular(10);

        assertEquals(popular.size(), 2);
        assertEquals(popular.get(0).getId(), 2);
        assertEquals(popular.get(1).getId(), 1);
    }

    @Test
    public void testGetGenreById() {
        ResponseEntity<Genre> genre = genreController.getGenre(1);

        assertEquals(genre.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(genre.getBody()).getId(), 1);
    }

    @Test
    public void testGetMpaById() {
        ResponseEntity<Mpa> mpa = mpaController.getMpa(1);

        assertEquals(mpa.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(mpa.getBody()).getId(), 1);
    }
}
