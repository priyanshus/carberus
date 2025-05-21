package com.cb.carberus.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(httpRequest -> {
                    httpRequest.requestMatchers("/").permitAll();
                    httpRequest.requestMatchers("/login").permitAll();
                    httpRequest.requestMatchers("/signup").permitAll();
                    httpRequest.requestMatchers("/api/v1/**").authenticated();
                    httpRequest.anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
