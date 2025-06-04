package com.cb.carberus.errorHandler.error;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException() {
        super("Not authorized to perform this action");
    }
}
