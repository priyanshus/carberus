package com.cb.carberus.security.config;

import com.cb.carberus.auth.service.AuthUserDetailsService;
import com.cb.carberus.config.CustomUserDetails;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.errorHandler.error.StandardApiException;
import com.cb.carberus.errorHandler.model.StandardErrorCode;
import com.cb.carberus.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthUserDetailsService userDetailsService;

    @Autowired
    private UserContext userContext;

    @Autowired
    private JwtUtil jwtUtil;

    public JwtAuthorizationFilter(AuthUserDetailsService userDetailsService, UserContext userContext, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.userContext = userContext;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = null;

        if (checkForOpenPaths(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        token = extractToken(request);
        if (token == null || token.isEmpty()) {
            throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
        }

        Map<String, Object> decodeJwt = jwtUtil.validateToken(token);
        String email = decodeJwt.get("subject").toString();

        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext()
                .setAuthentication(authToken);

        UserRole userRole = getRole(userDetails);
        userContext.setUser(userDetails.getUser());
        userContext.setRole(userRole);
        userContext.setUserId(userDetails.getUserId());
        filterChain.doFilter(request, response);
    }

    private UserRole getRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // e.g., "ADMIN"
                .map(String::toUpperCase)            // match enum casing if needed
                .map(roleStr -> {
                    try {
                        return UserRole.valueOf(roleStr);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No valid role found"));
    }

    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private boolean checkForOpenPaths(HttpServletRequest request) {
        String path = request.getRequestURI();

        if (path.equals("/api/v1/login") ||
                path.equals("/api/v1/signup") ||
                path.startsWith("/api/v1/swagger-ui") ||
                path.startsWith("/api/v1/v3/api-docs")) {

            log.info("Allowed path: {}", path);
            return true;
        }

        return false;
    }
}