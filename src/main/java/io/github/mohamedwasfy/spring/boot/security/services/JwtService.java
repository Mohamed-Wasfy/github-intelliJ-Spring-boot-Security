package io.github.mohamedwasfy.spring.boot.security.services;

import io.github.mohamedwasfy.spring.boot.security.config.JwtConfig;
import io.github.mohamedwasfy.spring.boot.security.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Date;

@AllArgsConstructor
@Service
public class JwtService {
    private final JwtConfig jwtConfig;

    public String generateAccessToken(User user) {
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
//        final long tokenExpiration = 300; // 5m
//        return generateAccessToken(user, tokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
//        final long tokenExpiration = 604800; // 7d

//        return generateToken(user, tokenExpiration);
    }

    private String generateToken(
            User user,
            long tokenExpiration
    ) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            var claims = getClaims(token);

            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
//            ex.printStackTrace();
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }
}

// ACCESS TOKEN.
// To access protected endpoints
// Short-lived.
// Returned in the response body.
// Stored in memory or localStorage.
// localStorage is less secure.

//

// Refresh TOKEN.
// To get a new access token.
// Long-lived [7d or more].
// Returned as an HttpOnly cookie.
// Not accessible via JavaScript.
// Much harder to steal.

// ISSUING REFRESH TOKENS.

