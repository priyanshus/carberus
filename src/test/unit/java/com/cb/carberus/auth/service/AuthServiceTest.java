package com.cb.carberus.auth.service;

import com.cb.carberus.auth.dto.LoginRequestDTO;
import com.cb.carberus.config.CustomUserDetails;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.errorHandler.error.AuthenticationFailedException;
import com.cb.carberus.security.jwt.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    AuthUserDetailsService authUserDetailsService;

    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @Mock
    UserContext userContext;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthService authService;

    @Test
    void shouldAuthenticateWhenCredentialsAreValid() {
        // Arrange
        String email = "something@mail.com";
        String rawPassword = "password";

        LoginRequestDTO mockLoginRequest = new LoginRequestDTO();
        mockLoginRequest.setEmail(email);
        mockLoginRequest.setPassword(rawPassword);

        CustomUserDetails userDetails = Mockito.mock(CustomUserDetails.class);

        Mockito.when(authUserDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        Mockito.when(userDetails.getPassword()).thenReturn("encodedPassword");
        Mockito.when(passwordEncoder.matches(rawPassword, "encodedPassword")).thenReturn(true);
        Mockito.when(jwtUtil.generateToken(userDetails)).thenReturn("some-token");

        // Act
        String jwtToken = authService.authenticate(mockLoginRequest);

        // Assert
        Assertions.assertEquals("some-token", jwtToken);
    }

    @Test
    void shouldThrowAuthenticationFailedExceptionWhenCredentialsAreInvalid() {
        // Arrange
        String email = "something@mail.com";
        String rawPassword = "password";

        LoginRequestDTO mockLoginRequest = new LoginRequestDTO();
        mockLoginRequest.setEmail(email);
        mockLoginRequest.setPassword(rawPassword);

        CustomUserDetails userDetails = Mockito.mock(CustomUserDetails.class);

        Mockito.when(authUserDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        Mockito.when(userDetails.getPassword()).thenReturn("encodedPassword");
        Mockito.when(passwordEncoder.matches(rawPassword, "encodedPassword")).thenReturn(false);

        // Act
        Assertions.assertThrows(AuthenticationFailedException.class, () -> {
            authService.authenticate(mockLoginRequest);
        });
    }
}
