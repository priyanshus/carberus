package com.cb.carberus.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class LoginRequestDTOTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void whenFieldsAreBlank_thenValidationFails() {
        LoginRequestDTO dto = new LoginRequestDTO(); // blank email + password
        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    void whenEmailIsInvalid_thenValidationFails() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("a");
        dto.setPassword("tests");
        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Assertions.assertFalse(violations.isEmpty());
    }
}
