package com.shopkart.shopkartauthenticationservice.utilities;

import com.shopkart.shopkartauthenticationservice.exceptions.SessionExpiredException;
import com.shopkart.shopkartauthenticationservice.exceptions.UnAuthorizedException;
import com.shopkart.shopkartauthenticationservice.services.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.token.key}")
    private String SECRET_KEY;
    @Value("${jwt.token.expiration.time}")
    private long EXPIRATION_TIME;

    private AuthService authService;

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public String generateToken(Map<String, ?> claimsMap) {
        return Jwts.builder().claims(claimsMap)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .issuedAt(new Date())
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date(System.currentTimeMillis()));
        } catch (ExpiredJwtException eje) {
            Long sessionId = eje.getClaims().get("sid", Long.class);
            if (authService.deleteTokenFromDb(sessionId))
                throw new SessionExpiredException("Token Expired-Utility");
            throw new UnAuthorizedException("Invalid Token - Utility-2");
        } catch (JwtException je) {
            throw new UnAuthorizedException("Invalid Token-Utility");
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong-Utility");
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}