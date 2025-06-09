package com.shopkart.shopkartauthenticationservice.dtos;

import java.util.List;

public record UserRecord(String userId, String email, List<String> roles) {
}
