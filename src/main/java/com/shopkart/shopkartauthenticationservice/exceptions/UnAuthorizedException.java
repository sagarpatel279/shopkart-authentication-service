package com.shopkart.shopkartauthenticationservice.exceptions;

public class UnAuthorizedException extends RuntimeException {
    public UnAuthorizedException(String message){
        super(message);
    }
}
