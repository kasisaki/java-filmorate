package ru.yandex.practicum.filmorate.exception.dbException;

public class IllegalGenreException extends RuntimeException {
    public IllegalGenreException() {
    }

    public IllegalGenreException(String message) {
        super(message);
    }

    public IllegalGenreException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalGenreException(Throwable cause) {
        super(cause);
    }

    public IllegalGenreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
