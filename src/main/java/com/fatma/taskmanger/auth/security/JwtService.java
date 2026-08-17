package com.fatma.taskmanger.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Everything JWT: signing key, generating access/refresh tokens, and
 * parsing + validating tokens on the way back in.
 *
 * generateToken() takes a UserDetails (not our User entity) on purpose -
 * "program to an interface, not an implementation" - JWT generation
 * shouldn't care whether the user came from the database, LDAP or OAuth.
 */
@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private SecretKey getSigningKey() {
        // jwtProperties.secret() is a Base64 string; JJWT needs raw bytes.
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /** Short-lived token sent with every request, carries the role claim. */
    public String generateToken(UserDetails userDetails) {
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("USER");

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.accessExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Long-lived token, only ever sent to /auth/refresh. No role claim -
     * it isn't used for authorization, only to prove "this is the same
     * user who logged in before".
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.refreshExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
