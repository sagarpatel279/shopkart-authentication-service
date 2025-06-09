package com.shopkart.shopkartauthenticationservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SignUpRequestRecord(
        @NotNull(message = "Email must be required to sign up")
        @Email(message = "Invalid Email Address")
        String email,
        @NotNull(message = "Password must be required to sign up")
        String password) {
}
