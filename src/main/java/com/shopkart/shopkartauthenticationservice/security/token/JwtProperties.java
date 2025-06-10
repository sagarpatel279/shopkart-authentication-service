package com.shopkart.shopkartauthenticationservice.security.token;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

@Getter
@Setter
@Component
public class JwtProperties {
    @Value("${jwt.token.key.encoded.base64}")
    private String encodedBase64Key;
    @Value("${jwt.token.expiration.time.days}")
    private int tokenExpirationDays = 7;
    @Value("${jwt.token.issuer}")
    private String issuer;

    public SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(this.encodedBase64Key);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (WeakKeyException e) {
            throw new IllegalStateException("The secret key is too weak. It must be at least 256 bits.", e);
        }
    }
}
