package com.webdevlab.tablicabackend.exception.user;

public class UnauthorizedUserAccessException extends RuntimeException {
    public UnauthorizedUserAccessException(String message) {
        super(message);
    }
}
