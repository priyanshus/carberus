package com.cb.carberus.errorHandler.error;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailedException extends AuthenticationException {
    public AuthenticationFailedException() {
        super("Authentication failed");
    }
}
