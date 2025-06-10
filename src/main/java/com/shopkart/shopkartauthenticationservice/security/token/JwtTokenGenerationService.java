package com.shopkart.shopkartauthenticationservice.security.token;


import com.shopkart.shopkartauthenticationservice.utilities.DateUtility;
import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.Map;

public class JwtTokenGenerationService{
    private Map<String,Object> claims;
    private Date issuedAt;
    private String subject;
    private String sessionId;
    private JwtProperties jwtProperties;

    private JwtTokenGenerationService(Builder builder){
        this.claims=builder.claims;
        this.issuedAt=builder.issuedAt;
        this.subject=builder.subject;
        this.sessionId=builder.sessionId;
        this.jwtProperties=builder.jwtProperties;
    }

    public static class Builder{
        private Map<String,Object> claims;
        private Date issuedAt;
        private String subject;
        private String sessionId;
        private JwtProperties jwtProperties;

        public Builder jwtProperties(JwtProperties jwtProperties){
            this.jwtProperties=jwtProperties;
            return this;
        }

        public Builder claims(Map<String,Object> claims){
            this.claims=claims;
            return this;
        }
        public Builder issuedAt(Date issuedAt){
            this.issuedAt=issuedAt;
            return this;
        }
        public Builder subject(String subject){
            this.subject=subject;
            return this;
        }
        public Builder sessionId(String sessionId){
            this.sessionId=sessionId;
            return this;
        }
        public JwtTokenGenerationService build(){
            return new JwtTokenGenerationService(this);
        }
    }
    public String generateToken() {
        return Jwts.builder()
                .claims(this.claims)
                .subject(this.subject)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(this.issuedAt)
                .id(this.sessionId)
                .expiration(DateUtility.setExpirationAfter(this.issuedAt, jwtProperties.getTokenExpirationDays()))
                .signWith(jwtProperties.getSigningKey())
                .compact();
    }

}
