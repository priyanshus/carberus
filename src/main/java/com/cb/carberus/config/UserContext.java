package com.cb.carberus.config;

import com.cb.carberus.constants.UserRole;
import com.cb.carberus.user.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST,  proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserContext {
    private User user;
    private UserRole role;
    private Long userId;

    public void setRole(UserRole role) {
        if (this.role != null) {
            throw new IllegalStateException("hasRoles can only be set once");
        }

        this.role = role;
    }

    public UserRole getRole() {
        if (this.role == null) {
            throw new IllegalStateException("hasRoles not initialized");
        }

        return this.role;
    }

    public Long getUserId() {
        if (this.userId == null) {
            throw new IllegalStateException("userId not initialized");
        }

        return this.userId;
    }

    public void setUserId(Long userId) {
        if (this.userId != null) {
            throw new IllegalStateException("userId can only be set once");
        }

        this.userId = userId;
    }

    public void setUser(User user) {
        if (this.user != null) {
            throw new IllegalStateException("user can only be set once");
        }

        this.user = user;
    }

    public User getUser() {
        if (this.user == null) {
            throw new IllegalStateException("user not initialized");
        }

        return this.user;
    }

}
