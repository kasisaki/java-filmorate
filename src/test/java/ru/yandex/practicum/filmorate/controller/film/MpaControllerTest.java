package ru.yandex.practicum.filmorate.controller.film;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.service.film.MpaService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = MpaController.class)
public class MpaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MpaService mpaService;

    @Test
    public void testFindAllMpa() throws Exception {
        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetMpa() throws Exception {
        mockMvc.perform(get("/mpa/{id}", 1))
                .andExpect(status().isOk());
    }
}
