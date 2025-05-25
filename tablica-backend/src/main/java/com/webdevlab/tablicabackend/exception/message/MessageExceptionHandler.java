package com.webdevlab.tablicabackend.exception.message;

import com.webdevlab.tablicabackend.exception.offer.OfferNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class MessageExceptionHandler {
    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleMessageNotFoundException(MessageNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedMessageAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedMessageAccessException(UnauthorizedMessageAccessException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(SelfMessagingNotAllowedException.class)
    public ResponseEntity<Map<String, String>> handleSelfMessagingNotAllowedException(SelfMessagingNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", ex.getMessage()));
    }
}
