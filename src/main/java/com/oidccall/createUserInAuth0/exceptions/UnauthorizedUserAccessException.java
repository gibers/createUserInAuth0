package com.oidccall.createUserInAuth0.exceptions;

public class UnauthorizedUserAccessException extends RuntimeException {
    public UnauthorizedUserAccessException(String message) {
        super(message);
    }
}
