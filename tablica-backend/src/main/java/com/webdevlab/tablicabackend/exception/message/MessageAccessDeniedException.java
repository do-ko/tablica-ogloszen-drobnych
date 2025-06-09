package com.webdevlab.tablicabackend.exception.message;

public class MessageAccessDeniedException extends RuntimeException {
    public MessageAccessDeniedException(String message) {
        super(message);
    }
}
