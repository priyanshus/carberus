package com.cb.carberus.errorHandler.error;

import org.springframework.security.core.AuthenticationException;

public class InvalidAuthenticationTokenException extends AuthenticationException {
    public InvalidAuthenticationTokenException() {
        super("Invalid Token");
    }
}
