package com.cb.carberus.security.config;

import com.cb.carberus.auth.service.AuthUserDetailsService;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.Role;
import com.cb.carberus.errorHandler.error.AuthenticationFailedException;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthUserDetailsService userDetailsService;

    @Autowired
    private UserContext userContext;

    @Autowired
    private JwtUtil jwtUtil;

    public JwtAuthorizationFilter( AuthUserDetailsService userDetailsService, UserContext userContext, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.userContext = userContext;
        this.jwtUtil = jwtUtil;
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
        System.out.println(token);
        if (token == null || token.isEmpty()) {
            throw new AuthenticationFailedException();
        }

        Map<String, Object> decodeJwt  = jwtUtil.validateToken(token);
        String email = decodeJwt.get("subject").toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        List<Role> userRoles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(Role::valueOf)
                .collect(Collectors.toList());
        userContext.setRoles(userRoles);
        filterChain.doFilter(request, response);
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
}