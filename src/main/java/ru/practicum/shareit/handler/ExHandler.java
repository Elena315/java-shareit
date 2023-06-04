package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

@Slf4j
@RestControllerAdvice
public class ExHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleStorageException(NotFoundException e) {
        log.error("Storage error - object not found" + "\n" + e.getMessage());
        return new ErrorResponse("NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemException(AvailableException e) {
        log.error("Storage error - item not found or not available" + "\n" + e.getMessage());
        return new ErrorResponse("NOT_AVAILABLE", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingException(ValidationException e) {
        log.error("Storage error - incorrect request" + "\n" + e.getMessage());
        return new ErrorResponse(e.getMessage(), "incorrect request");
    }
}