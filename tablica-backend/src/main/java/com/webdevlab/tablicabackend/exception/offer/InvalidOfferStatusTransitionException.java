package com.webdevlab.tablicabackend.exception.offer;

public class InvalidOfferStatusTransitionException extends RuntimeException {
    public InvalidOfferStatusTransitionException(String message) {
        super(message);
    }
}
