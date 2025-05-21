package com.webdevlab.tablicabackend.exception.offer;

public class UnauthorizedOfferAccessException extends RuntimeException {
    public UnauthorizedOfferAccessException(String message) {
        super(message);
    }
}
