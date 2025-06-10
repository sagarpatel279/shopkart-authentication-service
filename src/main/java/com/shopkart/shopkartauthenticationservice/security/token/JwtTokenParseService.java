package com.shopkart.shopkartauthenticationservice.security.token;

import com.shopkart.shopkartauthenticationservice.exceptions.ExpiredTokenException;
import com.shopkart.shopkartauthenticationservice.exceptions.InvalidTokenException;
import io.jsonwebtoken.*;

import java.util.*;

public class JwtTokenParseService {
    private Claims claims;
    private TokenState tokenState;
    private JwtProperties jwtProperties;

    private JwtTokenParseService(Builder builder){
        jwtProperties=builder.jwtProperties;
        validate(builder.token);

    }
    private void validate(String token){
        try {
            this.claims =Jwts.parser()
                    .verifyWith(jwtProperties.getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            tokenState=TokenState.VALID;
        } catch (ExpiredJwtException e) {
            tokenState=TokenState.EXPIRED;
        } catch (JwtException e) {
            tokenState=TokenState.INVALID_SIGNATURE;
        }
    }

    public static class Builder{
        private String token;
        private JwtProperties jwtProperties;

        public Builder jwtProperties(JwtProperties jwtProperties){
            this.jwtProperties=jwtProperties;
            return this;
        }

        public Builder token(String token){
            this.token=token;
            return this;
        }

        public JwtTokenParseService build(){
            return new JwtTokenParseService(this);
        }
    }
    public Optional<Map<String, Object>> getAllClaimsFromToken() {
        return Optional.of(this.claims);
    }

    public String getSubject() {
        return claims.getSubject();
    }
    public String getSessionId(){
        return claims.getId();
    }
    public TokenState getTokenState() {
        return this.tokenState;
    }

}
