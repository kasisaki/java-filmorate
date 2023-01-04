package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    @NotNull(message = "Release date must not be null")
    public final LocalDate releaseDate;
    @NotBlank(message = "Name must not be empty")
    private final String name;
    @NotNull(message = "Duration must not be null")
    @Positive
    private final int duration;
    @Size(max = 200, message = "Description is too long")
    public String description;
    private long id;
}
