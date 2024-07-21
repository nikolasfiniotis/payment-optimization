package com.example.paymentoptimization.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    public void testHandleGlobalException() {
        RuntimeException e = new RuntimeException("Test Exception");

        ResponseEntity<?> response = exceptionHandler.handleRuntimeException(e);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Test Exception", response.getBody());
    }
}
