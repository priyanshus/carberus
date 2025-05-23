package com.cb.carberus.auth.service;

import com.cb.carberus.auth.dto.LoginRequestDTO;
import com.cb.carberus.config.error.AuthenticationFailedException;
import com.cb.carberus.user.repository.UserRepository;
import com.cb.carberus.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthUserDetailsService authUserDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(AuthUserDetailsService authUserDetailsService, BCryptPasswordEncoder passwordEncoder) {
        this.authUserDetailsService = authUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public String authenticate(LoginRequestDTO loginRequestDTO) {
        var userDetails = authUserDetailsService.loadUserByUsername(loginRequestDTO.getEmail());

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword())) {
            throw new AuthenticationFailedException();
        }
        return JwtUtil.generateToken(userDetails);
    }

}
