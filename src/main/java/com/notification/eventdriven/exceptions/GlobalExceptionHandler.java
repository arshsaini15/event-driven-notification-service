package com.notification.eventdriven.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotificationNotFound(
            NotificationNotFoundException ex
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                ex.getMessage()
        );
    }

    @ExceptionHandler(PermanentNotificationException.class)
    public ResponseEntity<Map<String, Object>> handlePermanentException(
            PermanentNotificationException ex
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "PERMANENT_FAILURE",
                ex.getMessage()
        );
    }

    @ExceptionHandler(TransientNotificationException.class)
    public ResponseEntity<Map<String, Object>> handleTransientException(
            TransientNotificationException ex
    ) {
        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "TRANSIENT_FAILURE",
                "Temporary failure. Please retry later."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex
    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String error,
            String message
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);

        return ResponseEntity
                .status(status)
                .body(body);
    }
}
