package com.desofs.backend.exceptions;

public class NotAuthorizedException extends Exception {
    public NotAuthorizedException(String message) {
        super(message);
    }
}
