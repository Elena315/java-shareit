package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleNoSuchServerException(Throwable throwable) {
        log.warn("500 {}", throwable.getMessage(), throwable);
        return Map.of(
                "error", "Ошибка сервера",
                "errorMessage", throwable.getMessage()
        );
    }
}