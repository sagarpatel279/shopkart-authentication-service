package com.shopkart.shopkartauthenticationservice.exceptions;

public class SessionExpiredException extends RuntimeException {
    public SessionExpiredException(String message){
        super(message);
    }
}
