package com.shopkart.shopkartauthenticationservice.security.token;

import java.util.Map;

public interface ITokenValidator {
    boolean validateToken(String token);
    boolean isTokenExpired(String token);
}