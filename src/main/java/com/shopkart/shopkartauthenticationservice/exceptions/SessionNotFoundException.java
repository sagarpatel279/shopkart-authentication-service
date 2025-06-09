package com.shopkart.shopkartauthenticationservice.exceptions;

public class SessionNotFoundException extends RuntimeException{
    public SessionNotFoundException(String message){
        super(message);
    }
}
