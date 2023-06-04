package ru.practicum.shareit.exceptionTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErroreResponseTest {
    private final ErrorResponse errorResponse = new ErrorResponse("error", "testDescription");

    @Test
    public void getError() {
        assertEquals("error", errorResponse.getError());
    }

    @Test
    public void getDescription() {
        assertEquals("testDescription", errorResponse.getDescription());
    }
}