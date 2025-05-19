package com.cb.carberus.auth.dto;

import lombok.Data;

import java.util.List;

@Data
public class SignupRequestDTO {
    private String email;
    private String password;
    private String confirmPassword;
    private List<String> roles;
}
