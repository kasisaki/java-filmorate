package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.dbException.DuplicateEmailException;
import ru.yandex.practicum.filmorate.exception.dbException.DuplicateLoginException;
import ru.yandex.practicum.filmorate.exception.dbException.FriendshipExistsException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

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
    public ResponseEntity<ErrorResponse> catchDuplicateEmailException(final DuplicateEmailException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(BAD_REQUEST.value(), e.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchDuplicateLoginException(final DuplicateLoginException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(BAD_REQUEST.value(), e.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchFriendshipExistsException(final FriendshipExistsException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(BAD_REQUEST.value(), e.getMessage()), BAD_REQUEST);
    }
}