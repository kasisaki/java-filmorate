package ru.yandex.practicum.filmorate.controller.film;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;

    private final Film testFilm = Film.builder()
            .name("test name")
            .mpa(Mpa.builder().id(1).build())
            .duration(100)
            .description("This is the test film")
            .releaseDate(LocalDate.now())
            .build();

    @Test
    public void testFindAll() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFilm() throws Exception {
        mockMvc.perform(get("/films/{id}", 1))
                .andExpect(status().isOk());
    }


    @Test
    public void testFindPopular() throws Exception {
        mockMvc.perform(get("/films/popular")
                .param("count", "5"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateFilm() throws Exception {
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testLikeFilm() throws Exception {
        mockMvc.perform(put("/films/{id}/like/{userId}", 1, 1))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        mockMvc.perform(put("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUnlike() throws Exception{
        mockMvc.perform(delete("/films/{id}/like/{userId}", 1, 1))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateFilmInvalidNameEmpty() throws Exception {
        Film testFilmEmptyName = Film.builder()
                .name("")
                .mpa(Mpa.builder().id(1).build())
                .duration(100)
                .description("This is the test film")
                .releaseDate(LocalDate.now())
                .build();

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(testFilmEmptyName)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateFilmInvalidDuration() throws Exception {
        Film testFilmEmptyName = Film.builder()
                .name("test name")
                .mpa(Mpa.builder().id(1).build())
                .duration(0)
                .description("This is the test film")
                .releaseDate(LocalDate.now())
                .build();

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(testFilmEmptyName)))
                .andExpect(status().isBadRequest());
    }
}