package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {
    private long id = IdGenerator.generate();
    @NotBlank(message = "Login must not be empty")
    private String login;
    private String name;
    @Email(message = "Invalid email")
    private String email;
    @NotNull(message = "Birthday must not be null")
    @Past(message = "Sorry, we do not host time travellers. Birthday must be from past")
    private LocalDate birthday;

}
