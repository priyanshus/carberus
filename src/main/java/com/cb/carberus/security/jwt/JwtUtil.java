package com.cb.carberus.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cb.carberus.config.CustomUserDetails;
import com.cb.carberus.errorHandler.error.StandardApiException;
import com.cb.carberus.errorHandler.model.StandardErrorCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final static String SECRET_KEY = "your-256-bit";


    public String generateToken(CustomUserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toUpperCase)
                .findFirst()
                .orElseThrow(() -> new StandardApiException(StandardErrorCode.UNAUTHORIZED));

        claims.put("role", role);

        return JWT.create()
                .withPayload(claims)
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public Map<String, Object> validateToken(String token) {
        if (token.isEmpty()) {
            throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
        }


        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
        DecodedJWT decodedJwt = null;


        try {
            decodedJwt = verifier.verify(token);
        }catch (JWTVerificationException e) {
            throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
        }


        String subject = decodedJwt.getSubject(); // typically the username
        Map<String, Object> claims = new HashMap<>();
        decodedJwt.getClaims().forEach((key, claim) -> claims.put(key, claim.as(Object.class)));

        // Package result
        Map<String, Object> result = new HashMap<>();
        result.put("subject", subject);
        result.put("claims", claims);

        return result;
    }
}
