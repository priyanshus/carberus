package com.cb.carberus.config;

import com.cb.carberus.errorHandler.error.UserNotFoundException;
import com.cb.carberus.user.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    @Getter
    private final User user;
    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getEmail();
        this.password = user.getPassword();

        if (user.getUserRole() == null) {
            throw new UserNotFoundException();
        }

        this.user = user;
        this.authorities = List.of(new SimpleGrantedAuthority(user.getUserRole().name()));
    }

    public Long getUserId() {
        return id;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
