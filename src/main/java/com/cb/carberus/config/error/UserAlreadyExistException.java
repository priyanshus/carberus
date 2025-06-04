package com.cb.carberus.config.error;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException() {
        super("User already exist");
    }
}
