package com.cb.carberus.security;

import com.cb.carberus.config.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditAwareImplementation implements AuditorAware<String> {
    private final UserContext userContext;

    @Autowired
    public AuditAwareImplementation(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(userContext.getUserId());
    }
}
