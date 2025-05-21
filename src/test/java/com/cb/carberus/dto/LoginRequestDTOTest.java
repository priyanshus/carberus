package com.cb.carberus.dto;

import com.cb.carberus.auth.dto.LoginRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class LoginRequestDTOTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void whenFieldsAreBlank_thenValidationFails() {
        LoginRequestDTO dto = new LoginRequestDTO(); // blank email + password
        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenEmailIsInvalid_thenValidationFails() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("a");
        dto.setPassword("tests");
        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
