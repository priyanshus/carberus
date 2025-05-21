package com.cb.carberus.auth.validator;

import com.cb.carberus.auth.dto.LoginRequestDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LoginRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return LoginRequestDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoginRequestDTO loginRequestDTO = (LoginRequestDTO) target;

        if(loginRequestDTO.getEmail().isEmpty() || !loginRequestDTO.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.rejectValue("email", "Invalid email format");
        }

        if(loginRequestDTO.getPassword().isEmpty()) {
            errors.rejectValue("password", "Password cannot be empty");
        }
    }
}
