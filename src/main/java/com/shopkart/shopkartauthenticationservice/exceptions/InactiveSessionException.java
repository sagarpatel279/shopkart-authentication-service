package com.shopkart.shopkartauthenticationservice.exceptions;

public class InactiveSessionException extends RuntimeException{
    public InactiveSessionException(String message){
        super(message);
    }
}
