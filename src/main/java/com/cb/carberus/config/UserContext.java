package com.cb.carberus.config;

import com.cb.carberus.constants.Role;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST,  proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserContext {
    private List<Role> roles;
    public void setRoles(List<Role> roles) {
        if (this.roles != null) {
            throw new IllegalStateException("hasRoles can only be set once");
        }

        this.roles = Collections.unmodifiableList(new ArrayList<>(roles));
    }

    public List<Role> getRoles() {
        if (roles == null) {
            throw new IllegalStateException("hasRoles not initialized");
        }
        return roles;
    }
}
