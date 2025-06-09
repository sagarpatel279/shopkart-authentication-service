package com.shopkart.shopkartauthenticationservice.security.token;

public interface ITokenValidator {
    TokenState validateToken(String token);
    boolean isTokenExpired(String token);
}