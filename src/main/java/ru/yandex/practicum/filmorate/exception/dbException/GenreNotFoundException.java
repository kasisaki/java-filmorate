package ru.yandex.practicum.filmorate.exception.dbException;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException() {
    }

    public GenreNotFoundException(String message) {
        super(message);
    }

    public GenreNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenreNotFoundException(Throwable cause) {
        super(cause);
    }

    public GenreNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
