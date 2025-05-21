package com.cb.carberus.config.error;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("Resource Not Found");
    }
}
