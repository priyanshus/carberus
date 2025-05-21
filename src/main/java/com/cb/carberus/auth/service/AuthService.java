package com.cb.carberus.auth.service;

import com.cb.carberus.auth.dto.CurrentUserResponseDTO;
import com.cb.carberus.auth.dto.LoginRequestDTO;
import com.cb.carberus.auth.mapper.Mapper;
import com.cb.carberus.config.error.AuthenticationFailedException;
import com.cb.carberus.config.error.UserNotFoundException;
import com.cb.carberus.repository.user.UserRepository;
import com.cb.carberus.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthUserDetailsService authUserDetailsService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(AuthUserDetailsService authUserDetailsService, BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.authUserDetailsService = authUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public String authenticate(LoginRequestDTO loginRequestDTO) {
        var userDetails = authUserDetailsService.loadUserByUsername(loginRequestDTO.getEmail());

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword())) {
            throw new AuthenticationFailedException();
        }
        return JwtUtil.generateToken(userDetails);
    }

    public CurrentUserResponseDTO getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationFailedException();
        }
        var userDetails = (UserDetails) authentication.getPrincipal();
        var email = userDetails.getUsername();
        var user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return Mapper.toCurrentUserResponse(user);
    }

}
