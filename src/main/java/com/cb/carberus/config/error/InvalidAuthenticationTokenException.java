package com.cb.carberus.config.error;

import org.springframework.security.core.AuthenticationException;

public class InvalidAuthenticationTokenException extends AuthenticationException {
    public InvalidAuthenticationTokenException() {
        super("Invalid Token");
    }
}
