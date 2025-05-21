package com.cb.carberus.config.error;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailedException extends AuthenticationException {
    public AuthenticationFailedException() {
        super("Authentication failed");
    }
}
