package com.cb.carberus.auth.controller;

import com.cb.carberus.auth.dto.LoginRequestDTO;
import com.cb.carberus.auth.service.AuthService;
import com.cb.carberus.auth.service.AuthUserDetailsService;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.security.jwt.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @MockitoBean
    private AuthUserDetailsService authUserDetailsService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserContext userContext;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCompleteLoginSuccessfully() throws Exception {
        // Arrange
        String token = "mock-jwt-token";
        Mockito.when(authService.authenticate(ArgumentMatchers.any(LoginRequestDTO.class))).thenReturn(token);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@a.com\", \"password\":\"tests\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(org.springframework.http.HttpHeaders.SET_COOKIE, Matchers.containsString("token=")))
                .andExpect(MockMvcResultMatchers.header().string("Authorization", "Bearer " + token));
    }

    @Test
    void shouldReturnError_WhenEmailValidationFails() throws Exception {
        // Arrange
        String token = "mock-jwt-token";
        Mockito.when(authService.authenticate(ArgumentMatchers.any(LoginRequestDTO.class))).thenReturn(token);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a\", \"password\":\"tests\"}"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void shouldReturnError_WhenPasswordValidationFails() throws Exception {
        // Arrange
        String token = "mock-jwt-token";
        Mockito.when(authService.authenticate(ArgumentMatchers.any(LoginRequestDTO.class))).thenReturn(token);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@a.com\", \"password\":\"\"}"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

}
