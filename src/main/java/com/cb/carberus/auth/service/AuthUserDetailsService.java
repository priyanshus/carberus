package com.cb.carberus.auth.service;

import com.cb.carberus.config.CustomUserDetails;
import com.cb.carberus.errorHandler.error.UserNotFoundException;
import com.cb.carberus.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public AuthUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = this.userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        return new CustomUserDetails(user);
    }


}
