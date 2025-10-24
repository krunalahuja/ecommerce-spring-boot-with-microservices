package com.ecommerce.auth_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtService {

    private final Key secretKey = Keys.hmacShaKeyFor(
            "dnewwjnwjnjnajxsmxWEMWQJHBDHmswijowiefjgntrjndfjvndcefkefsocdfobjopSWKPQPAZ".getBytes()
    );

    private final long expirationMs = 1000 * 60 * 60; // 1 hour

    // Generate JWT with email and roles
    public String generateToken(String email, List<String> roles) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getEmail(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public List<String> getRoles(String token) {
        Claims claims = parseClaims(token);
        return claims.get("roles", List.class);
    }

    public boolean validateToken(String token, String email) {
        return getEmail(token).equals(email) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
