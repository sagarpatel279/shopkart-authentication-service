package com.shopkart.shopkartauthenticationservice.security.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class JwtProperties {
    @Value("${jwt.token.key}")
    private String secretKey;
    @Value("${jwt.token.expiration.time.days}")
    private int tokenExpirationDays = 7;
    @Value("${jwt.token.issuer}")
    private String issuer;
}
