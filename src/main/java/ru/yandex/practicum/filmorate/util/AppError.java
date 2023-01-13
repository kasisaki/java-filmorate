package ru.yandex.practicum.filmorate.util;

import lombok.Data;

@Data
public class AppError {
    private int statusCode;
    private String message;
}
