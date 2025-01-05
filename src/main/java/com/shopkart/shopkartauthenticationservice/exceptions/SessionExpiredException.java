package com.shopkart.shopkartauthenticationservice.exceptions;

public class SessionExpiredException extends Exception{
    public SessionExpiredException(String message){
        super(message);
    }
}
