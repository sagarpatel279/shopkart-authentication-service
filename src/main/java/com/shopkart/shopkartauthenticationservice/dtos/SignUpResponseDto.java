package com.shopkart.shopkartauthenticationservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpResponseDto {
    private String message;
    private ResponseStatus responseStatus;
}
