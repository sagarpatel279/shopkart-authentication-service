package com.shopkart.shopkartauthenticationservice.services;

public interface AuthenticationServices {
    String login(String email, String password, String ipAddr);
    void logout(String token);
    boolean validateToken(String token);
    boolean signUp(String email, String password);
}
