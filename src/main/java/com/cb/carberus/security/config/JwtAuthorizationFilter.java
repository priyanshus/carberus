package com.cb.carberus.security.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.cb.carberus.auth.service.AuthUserDetailsService;
import com.cb.carberus.config.error.AuthenticationFailedException;
import com.cb.carberus.config.error.UserNotFoundException;
import com.cb.carberus.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthUserDetailsService userDetailsService;

    public JwtAuthorizationFilter( AuthUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String path = request.getServletPath();

        if (path.equals("/") || path.equals("/login") || path.equals("/signup")) {
            filterChain.doFilter(request, response);
            return;
        }


        token = extractToken(request);

        assert token != null;
        if (token.isEmpty()) {
            throw new AuthenticationFailedException();
        }

        Map<String, Object> decodeJwt  = JwtUtil.validateToken(token);
        String email = decodeJwt.get("subject").toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}