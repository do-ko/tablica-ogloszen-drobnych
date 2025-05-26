package com.webdevlab.tablicabackend.exception.message;

public class UnauthorizedMessageAccessException extends RuntimeException {
    public UnauthorizedMessageAccessException(String message) {
        super(message);
    }
}
