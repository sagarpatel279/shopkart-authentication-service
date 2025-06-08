package com.shopkart.shopkartauthenticationservice.security.token;

import com.shopkart.shopkartauthenticationservice.utilities.DateUtility;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenServiceImpl implements ITokenService{
    private final JwtProperties jwtProperties;

    public JwtTokenServiceImpl(JwtProperties jwtProperties){
        this.jwtProperties=jwtProperties;
    }

    @Override
    public Map<String, Object> getAllClaimsFromToken(String token) {
        return Map.of();
    }

    @Override
    public String getSubjectFromToken(String token) {
        return "";
    }

    @Override
    public Date getExpirationDateFromToken(String token) {
        return null;
    }

    @Override
    public String generateToken(Map<String, Object> claims) {
        Date issuedAt=new Date();
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
        Date issuedAt=new Date();
        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiration(DateUtility.setExpirationAfter(issuedAt, jwtProperties.getTokenExpirationDays()))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public boolean isTokenExpired(String token) {
        return false;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
