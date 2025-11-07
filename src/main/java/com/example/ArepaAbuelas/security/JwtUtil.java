package com.example.ArepaAbuelas.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key = Keys.hmacShaKeyFor("MiSecretoSuperLargoParaJWT_UsaVariableEntorno".getBytes());
    private final long EXPIRATION_MS = 1000 * 60 * 60 * 6; // 6 horas

    public String generateToken(String username, String role, boolean approved) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("approved", approved)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    public Claims getAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public String getRole(String token) {
        return getAllClaims(token).get("role", String.class);
    }

    public Boolean getApproved(String token) {
        return getAllClaims(token).get("approved", Boolean.class);
    }

    public boolean validateToken(String token) {
        try {
            getAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
