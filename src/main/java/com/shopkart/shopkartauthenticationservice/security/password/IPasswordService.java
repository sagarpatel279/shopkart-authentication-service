package com.shopkart.shopkartauthenticationservice.security.password;

public interface IPasswordService {
    String generateSaltPassword(String rawPassword);
    boolean matches(String encodedPassword, String rawPassword);
}
