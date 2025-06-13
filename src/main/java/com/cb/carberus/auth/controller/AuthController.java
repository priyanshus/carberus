package com.cb.carberus.auth.controller;

import com.cb.carberus.auth.dto.LoginRequestDTO;
import com.cb.carberus.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;


@RestController
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<Void> postLogin(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        String token = authService.authenticate(loginRequestDTO);
        ResponseCookie cookie = createCookie(token);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader("Authorization", "Bearer " + token);

        return ResponseEntity.ok().build();
    }

    private ResponseCookie createCookie(String token) {
        return ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

    }
}
