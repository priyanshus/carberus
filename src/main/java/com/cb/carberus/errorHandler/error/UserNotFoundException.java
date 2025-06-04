package com.cb.carberus.errorHandler.error;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends UsernameNotFoundException {
    public UserNotFoundException() {
        super("User not found");
    }
}
