package com.shopkart.shopkartauthenticationservice.dtos;


public class ApiResponse<T> {
    private String message;
    private T data;
    private ResponseStatus status;
}
