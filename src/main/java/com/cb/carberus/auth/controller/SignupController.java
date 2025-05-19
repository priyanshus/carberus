package com.cb.carberus.auth.controller;

import com.cb.carberus.auth.dto.SignupRequestDTO;
import com.cb.carberus.auth.dto.SignupResponseDTO;
import com.cb.carberus.auth.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignupController {
    private final SignupService signupService;

    @Autowired
    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO signupRequestDTO) {
        SignupResponseDTO response = signupService.handleSignup(signupRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}
