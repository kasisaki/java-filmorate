package ru.yandex.practicum.filmorate.exception.dbException;

public class MpaNotFoundException extends RuntimeException {
    public MpaNotFoundException() {
    }

    public MpaNotFoundException(String message) {
        super(message);
    }

    public MpaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MpaNotFoundException(Throwable cause) {
        super(cause);
    }

    public MpaNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
