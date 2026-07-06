package com.newevent.registrationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Same consistent error-shape pattern as Event Service and Program Service,
 * extended to cover errors that originate downstream (from Event Service)
 * as well as local ones - the caller of this API shouldn't need to know or
 * care which service actually produced the error.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegistrationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RegistrationNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEventNotFound(EventNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InsufficientSeatsException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientSeats(InsufficientSeatsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EventServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleEventServiceUnavailable(EventServiceUnavailableException ex) {
        // 503, not 500 - this is "a downstream dependency is down", not
        // "this service has a bug". A real distinction worth defending
        // if asked about non-functional design (availability/resilience).
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
