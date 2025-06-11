package com.cb.carberus.errorHandler.controller;

import com.cb.carberus.errorHandler.dto.ErrorResponseDTO;
import com.cb.carberus.errorHandler.error.*;
import com.cb.carberus.errorHandler.model.StandardErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalAdviseController {

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponseDTO> userNotFound(AuthenticationFailedException exception) {
        return new ResponseEntity<>(new ErrorResponseDTO(exception.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        errors.put("errorCode", StandardErrorCode.INVALID_INPUT.getErrorCode());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> authenticationNotFound(UserNotFoundException exception) {
        return new ResponseEntity<>(new ErrorResponseDTO(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponseDTO> unauthorizedAccess(UnauthorizedAccessException exception) {
        return new ResponseEntity<>(new ErrorResponseDTO(exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDTO> unauthorizedAccess(UserAlreadyExistException exception) {
        return new ResponseEntity<>(new ErrorResponseDTO(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AbstractApiException.class)
    public ResponseEntity<ErrorResponseDTO> applicationError(AbstractApiException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> applicationError(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO("INVALID_INPUT"), HttpStatus.BAD_REQUEST);
    }
}
