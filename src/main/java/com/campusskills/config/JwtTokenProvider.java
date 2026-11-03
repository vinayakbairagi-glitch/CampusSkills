package com.campusskills.config;

import com.campusskills.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final CollegeProperties collegeProperties;
    private final SecretKey key;

    public JwtTokenProvider(CollegeProperties collegeProperties) {
        this.collegeProperties = collegeProperties;
        String secret = collegeProperties.getJwt().getSecret();
        if (secret == null || secret.length() < 32) throw new IllegalStateException("JWT_SECRET must be at least 32 characters long.");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + collegeProperties.getJwt().getExpirationMs());
        return Jwts.builder().subject(user.getEmail()).claim("userId", user.getId())
                .claim("role", user.getRole().name()).claim("status", user.getStatus().name())
                .claim("rollNumber", user.getRollNumber()).issuedAt(now).expiration(expiryDate)
                .signWith(key).compact();
    }

    public String getEmailFromToken(String token) { return getClaims(token).getSubject(); }
    public Long getUserIdFromToken(String token) {
        Number id = getClaims(token).get("userId", Number.class);
        return id != null ? id.longValue() : null;
    }
    public boolean validateToken(String token) {
        try { Jwts.parser().verifyWith(key).build().parseSignedClaims(token); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }
    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
