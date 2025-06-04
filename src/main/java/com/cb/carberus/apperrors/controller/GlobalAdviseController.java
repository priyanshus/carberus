package com.cb.carberus.apperrors.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.cb.carberus.apperrors.dto.ErrorResponseDTO;
import com.cb.carberus.config.error.AuthenticationFailedException;
import com.cb.carberus.config.error.UnauthorizedAccessException;
import com.cb.carberus.config.error.UserAlreadyExistException;
import com.cb.carberus.config.error.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalAdviseController {

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponseDTO> userNotFound(AuthenticationFailedException exception) {
        return new ResponseEntity<>(new ErrorResponseDTO(exception.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO("provided payload is faulty"), HttpStatus.BAD_REQUEST);
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
        return new ResponseEntity<>(new ErrorResponseDTO(exception.getMessage()), HttpStatus.FORBIDDEN);
    }
}
