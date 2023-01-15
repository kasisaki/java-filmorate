package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.customAnnotation.IsAfter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.util.Constants.DATE_LIMIT;

@Data
@Builder
public class Film {
    @NotNull(message = "Release date must not be null")
    @IsAfter(dateLimit = DATE_LIMIT, message = "Wrong release date")
    public final LocalDate releaseDate;
    @NotBlank(message = "Name must not be empty")
    private final String name;
    @NotNull(message = "Duration must not be null")
    @Positive(message = "Duration must be a positive value")
    private final int duration;
    @Size(max = 200, message = "Description is too long")
    public String description;
    private long id;
}
