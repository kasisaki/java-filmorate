package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.dbException.FriendshipExistsException;
import ru.yandex.practicum.filmorate.exception.dbException.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.dbException.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.errorResponse.ErrorResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchBadRequestException(final BadRequestException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(BAD_REQUEST.value(), e.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchNotFoundException(final ElementNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(NOT_FOUND.value(), e.getMessage()), NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchFriendshipExistsException(final FriendshipExistsException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(BAD_REQUEST.value(), e.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchGenreException(final GenreNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(NOT_FOUND.value(), e.getMessage()), NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchMpaException(final MpaNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(NOT_FOUND.value(), e.getMessage()), NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchDuplicateKeyException(final DuplicateKeyException e) {
        log.warn(e.getCause().getLocalizedMessage());
        String field = "UNIQUE_KEY";
        if (e.getCause().getLocalizedMessage().contains("EMAIL")) {
            field = "EMAIL";
        } else if (e.getCause().getLocalizedMessage().contains("LOGIN")) {
            field = "LOGIN";
        }
        return new ResponseEntity<>(new ErrorResponse(BAD_REQUEST.value(), "Error. User with such " + field + " exists"), BAD_REQUEST);
    }
}