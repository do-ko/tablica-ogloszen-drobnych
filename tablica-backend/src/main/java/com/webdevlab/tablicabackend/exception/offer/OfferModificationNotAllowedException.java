package com.webdevlab.tablicabackend.exception.offer;

public class OfferModificationNotAllowedException extends RuntimeException {
    public OfferModificationNotAllowedException(String message) {
        super(message);
    }
}
