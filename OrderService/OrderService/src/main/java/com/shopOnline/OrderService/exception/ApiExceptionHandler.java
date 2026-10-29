package com.shopOnline.OrderService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(HttpStatus.NOT_FOUND, exception));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalid(InvalidRequestException exception) {
        return ResponseEntity.badRequest().body(error(HttpStatus.BAD_REQUEST, exception));
    }

    private Map<String, Object> error(HttpStatus status, Exception exception) {
        return Map.of("timestamp", LocalDateTime.now(), "status", status.value(),
                "error", status.getReasonPhrase(), "message", exception.getMessage());
    }
}
