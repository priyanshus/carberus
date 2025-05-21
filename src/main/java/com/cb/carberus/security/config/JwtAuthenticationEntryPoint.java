package com.cb.carberus.security.config;

import com.cb.carberus.apperrors.dto.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Authentication failed");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), errorResponseDTO);
    }
}
