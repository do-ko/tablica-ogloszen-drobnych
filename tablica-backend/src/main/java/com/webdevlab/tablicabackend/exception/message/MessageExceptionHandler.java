package com.webdevlab.tablicabackend.exception.message;

import com.webdevlab.tablicabackend.exception.offer.OfferNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class MessageExceptionHandler {

    @ExceptionHandler(ThreadNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleThreadNotFoundException(ThreadNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(MessageAccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleMessageAccessDeniedException(MessageAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", ex.getMessage()));
    }
}
