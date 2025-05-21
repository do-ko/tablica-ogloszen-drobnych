package com.webdevlab.tablicabackend.exception.offer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class OfferExceptionHandler {
    @ExceptionHandler(OfferNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleOfferNotFoundException(OfferNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedOfferAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedOfferAccessException(UnauthorizedOfferAccessException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(InvalidOfferStatusTransitionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidOfferStatusTransitionException(InvalidOfferStatusTransitionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(OfferModificationNotAllowedException.class)
    public ResponseEntity<Map<String, String>> handleOfferModificationNotAllowedException(OfferModificationNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
    }
}
