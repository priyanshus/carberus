package com.cb.carberus.auth.service;

import com.cb.carberus.auth.dto.LoginRequestDTO;
import com.cb.carberus.repository.user.UserRepository;
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

    public String isValidCredentials(LoginRequestDTO loginRequestDTO) {
        var userDetails = authUserDetailsService.loadUserByUsername(loginRequestDTO.getEmail());
//        if (passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword())) {
//            throw new RuntimeException("Invalid credentials");
//        }
        JwtUtil jwtUtil = new JwtUtil();
        return jwtUtil.generateToken(userDetails);
    }

    private boolean validateAuthenticationRequest(LoginRequestDTO loginRequestDTO) {
        if(loginRequestDTO == null) {
            return false;
        }

        if (loginRequestDTO.getEmail().isEmpty() || loginRequestDTO.getPassword().isEmpty()) {
            return false;
        }

        return true;
    }
}
