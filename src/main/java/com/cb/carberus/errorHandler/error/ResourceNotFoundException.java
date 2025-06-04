package com.cb.carberus.errorHandler.error;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("Resource Not Found");
    }
}
