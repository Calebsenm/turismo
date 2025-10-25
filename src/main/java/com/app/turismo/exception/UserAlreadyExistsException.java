package com.app.turismo.exception;

public class UserAlreadyExistsException extends ApiException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}