package com.webdevlab.tablicabackend.exception.message;

public class SelfMessagingNotAllowedException extends RuntimeException {
    public SelfMessagingNotAllowedException(String message) {
        super(message);
    }
}
