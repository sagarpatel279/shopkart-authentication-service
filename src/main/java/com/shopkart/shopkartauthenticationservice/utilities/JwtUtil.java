package com.shopkart.shopkartauthenticationservice.utilities;

import com.shopkart.shopkartauthenticationservice.exceptions.SessionExpiredException;
import com.shopkart.shopkartauthenticationservice.exceptions.UnAuthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.token.key}")
    private static String SECRET_KEY;
    @Value("${jwt.token.expiration.time}")
    private static long EXPIRATION_TIME;

    public String generateToken(Map<String, ?> claimsMap) {
        return Jwts.builder().claims(claimsMap)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .issuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if(claims.getExpiration().before(new Date(System.currentTimeMillis()))){
                throw new SessionExpiredException("Session is Expired Now");
            }
            return true;
        } catch (JwtException je) {
            throw new UnAuthorizedException("Invalid Token");
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
