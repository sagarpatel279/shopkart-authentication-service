package com.shopkart.shopkartauthenticationservice.exceptions;

public class ExpiredTokenException extends RuntimeException{
    public ExpiredTokenException(String message) {
        super(message);
    }
}
