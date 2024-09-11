package com.desofs.backend.exceptions;

public class RequestSizeLimitException extends RuntimeException {
    public RequestSizeLimitException(String message) {
        super(message);
    }
}