package com.shopkart.shopkartauthenticationservice.security.token;

import com.shopkart.shopkartauthenticationservice.utilities.DateUtility;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
@Primary
public class JwtTokenServiceImpl implements ITokenService{
    private final JwtProperties jwtProperties;
    private Jws<Claims> jwsClaims;
    public JwtTokenServiceImpl(JwtProperties jwtProperties){
        this.jwtProperties=jwtProperties;
    }

    @Override
    public Optional<Map<String, Object>> getAllClaimsFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(new HashMap<>(claims));
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Date> getExpirationDateFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims.getExpiration());
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    @Override
    public String getSubjectFromToken(String token) {
        Optional<Map<String,Object>> optionalMap = getAllClaimsFromToken(token);
        return optionalMap.isEmpty()?"": (String) optionalMap.get().get("sub");
    }

    @Override
    public String generateToken(Map<String, Object> claims) {
        Date issuedAt=DateUtility.getCurrentDate();
        return Jwts.builder()
                .claims(claims)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiration(DateUtility.setExpirationAfter(issuedAt, jwtProperties.getTokenExpirationDays()))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String generateToken() {
        Date issuedAt=DateUtility.getCurrentDate();
        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiration(DateUtility.setExpirationAfter(issuedAt, jwtProperties.getTokenExpirationDays()))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public TokenState validateToken(String token) {
        try{
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return TokenState.VALID;
        } catch (ExpiredJwtException e) {
            return TokenState.EXPIRED;
        } catch (JwtException e) {
            return TokenState.INVALID_SIGNATURE;
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try{
            Claims claims= Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token).getPayload();
            return claims.getExpiration().before(DateUtility.getCurrentDate());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getEncodedBase64Key());
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (WeakKeyException e) {
            throw new IllegalStateException("The secret key is too weak. It must be at least 256 bits.", e);
        }
    }

}
