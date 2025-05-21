package com.cb.carberus.controllers;

import com.cb.carberus.auth.controller.AuthController;
import com.cb.carberus.auth.dto.LoginRequestDTO;
import com.cb.carberus.auth.service.AuthService;
import com.cb.carberus.auth.service.AuthUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @MockitoBean
    private AuthUserDetailsService authUserDetailsService;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCompleteLoginSuccessfully() throws Exception {
        // Arrange
        String token = "mock-jwt-token";
        when(authService.authenticate(any(LoginRequestDTO.class))).thenReturn(token);

        // Act + Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@a.com\", \"password\":\"tests\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer " + token));
    }

    @Test
    void shouldReturnError_WhenEmailValidationFails() throws Exception {
        // Arrange
        String token = "mock-jwt-token";
        when(authService.authenticate(any(LoginRequestDTO.class))).thenReturn(token);

        // Act + Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a\", \"password\":\"tests\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnError_WhenPasswordValidationFails() throws Exception {
        // Arrange
        String token = "mock-jwt-token";
        when(authService.authenticate(any(LoginRequestDTO.class))).thenReturn(token);

        // Act + Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@a.com\", \"password\":\"\"}"))
                .andExpect(status().is4xxClientError());
    }

}
