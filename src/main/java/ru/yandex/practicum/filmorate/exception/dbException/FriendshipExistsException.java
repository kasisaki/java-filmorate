package ru.yandex.practicum.filmorate.exception.dbException;

public class FriendshipExistsException extends RuntimeException {
    public FriendshipExistsException() {
    }

    public FriendshipExistsException(String message) {
        super(message);
    }

    public FriendshipExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FriendshipExistsException(Throwable cause) {
        super(cause);
    }

    public FriendshipExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
