package com.cb.carberus.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cb.carberus.errorHandler.error.AuthenticationFailedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {
    private final static String SECRET_KEY = "your-256-bit";


    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        claims.put("roles", roles);

        return JWT.create()
                .withPayload(claims)
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public Map<String, Object> validateToken(String token) {
        if (token.isEmpty()) {
            throw new AuthenticationFailedException();
        }

        // Verify the token
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
        DecodedJWT decodedJwt = null;


        try {
            decodedJwt = verifier.verify(token);
        }catch (JWTVerificationException e) {
            throw new AuthenticationFailedException();
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
