package ru.yandex.practicum.filmorate.exception;

public class ValidationException extends Throwable {
    public ValidationException(String errorMessage) {
        super(errorMessage);
    }
}
