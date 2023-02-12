package ru.yandex.practicum.filmorate.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private final User testUser = User.builder()
            .login("testUser")
            .email("test@email.com")
            .birthday(LocalDate.of(1944, 2, 23))
            .build();


    @Test
    public void testFindAll() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUser() throws Exception {
        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllFriends() throws Exception{
        mockMvc.perform(get("/users/{id}/friends", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCommonFriends() throws Exception {
        mockMvc.perform(get("/users/{id}/friends/common/{friendId}", 1, 7))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateUser() throws Exception {
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUser() throws Exception {
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddFriend() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", 1, 7))
                .andExpect(status().isOk());
    }

    @Test
    public void testAcceptFriend() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}/status", 7, 1))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", 7))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteFriend() throws Exception {
        mockMvc.perform(delete("/users/{id}/friends/{friendId}", 1, 7))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateInvalidUserFromFuture() throws Exception {
        User invalidUserFromFuture = User.builder()
                .login("testUser")
                .email("test@email.com")
                .birthday(LocalDate.of(2044, 2, 23))
                .build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserFromFuture)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateInvalidUserEmptyLogin() throws Exception {
        User invalidUserEmptyLogin = User.builder()
                .login("")
                .email("test@email.com")
                .birthday(LocalDate.of(1944, 2, 23))
                .build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserEmptyLogin)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateInvalidUserWrongEmail() throws Exception {
        User invalidUserEmptyLogin = User.builder()
                .login("testLogin")
                .email("testATemail.com")
                .birthday(LocalDate.of(1944, 2, 23))
                .build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserEmptyLogin)))
                .andExpect(status().isBadRequest());
    }
}