package com.shopkart.shopkartauthenticationservice.security.token;

import java.util.Map;

public interface ITokenGenerator {
    String generateToken(Map<String,Object> claims);
    String generateToken();
}
