package ru.yandex.practicum.filmorate.exception.dbException;

public class IllegalMpaException extends RuntimeException {
    public IllegalMpaException() {
    }

    public IllegalMpaException(String message) {
        super(message);
    }

    public IllegalMpaException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalMpaException(Throwable cause) {
        super(cause);
    }

    public IllegalMpaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
