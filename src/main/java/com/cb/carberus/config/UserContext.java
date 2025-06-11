package com.cb.carberus.config;

import com.cb.carberus.constants.Role;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST,  proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserContext {
    private Role role;
    private String userId;

    public void setRole(Role role) {
        if (this.role != null) {
            throw new IllegalStateException("hasRoles can only be set once");
        }

        this.role = role;
    }

    public Role getRole() {
        if (this.role == null) {
            throw new IllegalStateException("hasRoles not initialized");
        }

        return this.role;
    }

    public String getUserId() {
        if (this.userId == null) {
            throw new IllegalStateException("userId not initialized");
        }

        return this.userId;
    }

    public void setUserId(String userId) {
        if (this.userId != null) {
            throw new IllegalStateException("userId can only be set once");
        }

        this.userId = userId;
    }
}
