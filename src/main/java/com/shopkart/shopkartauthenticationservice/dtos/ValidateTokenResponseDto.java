package com.shopkart.shopkartauthenticationservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenResponseDto {
    private String message;
    private ResponseStatus responseStatus;
}
