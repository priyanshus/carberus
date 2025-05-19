package com.cb.carberus.security.config;

import com.cb.carberus.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();

        if (path.equals("/") || path.equals("/login") || path.equals("/signup")) {
            System.out.println("🔐🔐Filtering login call");
            filterChain.doFilter(request, response);
            return;
        }

        if(Collections.list(request.getHeaderNames()).contains("Authorization")) {
            var token = extractToken(request);
            System.out.println(token);
            filterChain.doFilter(request, response);
        }

//        String token = extractToken(request);
//        String email = jwtUtil.extractUsername(token);
//        List<String> roles = jwtUtil.extractClaim(token, claims -> claims.get("roles", ArrayList.class));
//        System.out.println(roles);
//        Roles role = Roles.valueOf(roles.getFirst());
//
//        JwtUserPrincipal principal = new JwtUserPrincipal(email, role);
//
//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}