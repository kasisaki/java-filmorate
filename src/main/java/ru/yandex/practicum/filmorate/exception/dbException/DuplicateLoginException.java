package ru.yandex.practicum.filmorate.exception.dbException;

public class DuplicateLoginException extends RuntimeException {
    public DuplicateLoginException() {
    }

    public DuplicateLoginException(String message) {
        super(message);
    }

    public DuplicateLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateLoginException(Throwable cause) {
        super(cause);
    }

    public DuplicateLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
