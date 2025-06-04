package com.cb.carberus.errorHandler.error;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException() {
        super("User already exist");
    }
}
