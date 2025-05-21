package com.cb.carberus.auth.controller;

import com.cb.carberus.auth.dto.CurrentUserResponseDTO;
import com.cb.carberus.auth.dto.LoginRequestDTO;
import com.cb.carberus.auth.service.AuthService;
import com.cb.carberus.auth.validator.LoginRequestValidator;
import com.cb.carberus.config.error.AuthenticationFailedException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


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
        response.addHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponseDTO> getMe() {
        CurrentUserResponseDTO user = authService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

}
