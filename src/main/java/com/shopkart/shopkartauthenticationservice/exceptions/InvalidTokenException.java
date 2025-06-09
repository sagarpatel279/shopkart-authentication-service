package com.shopkart.shopkartauthenticationservice.exceptions;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String message){
        super(message);
    }
}
