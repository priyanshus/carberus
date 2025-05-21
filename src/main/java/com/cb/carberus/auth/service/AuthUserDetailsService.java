package com.cb.carberus.auth.service;

import com.cb.carberus.config.error.UserNotFoundException;
import com.cb.carberus.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public AuthUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = this.userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        if (authorities.isEmpty()) {
            throw new UserNotFoundException();
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
