package com.cb.carberus.auth.service;

import com.cb.carberus.auth.dto.LoginRequestDTO;
import com.cb.carberus.errorHandler.error.AuthenticationFailedException;
import com.cb.carberus.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthUserDetailsService authUserDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(AuthUserDetailsService authUserDetailsService, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authUserDetailsService = authUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String authenticate(LoginRequestDTO loginRequestDTO) {
        var userDetails = authUserDetailsService.loadUserByUsername(loginRequestDTO.getEmail());

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword())) {
            throw new AuthenticationFailedException();
        }

        return jwtUtil.generateToken(userDetails);
    }

}
