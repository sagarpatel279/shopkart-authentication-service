package com.shopkart.shopkartauthenticationservice.security.token;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

public interface ITokenService extends ITokenGenerator,ITokenValidator{
    Optional<Map<String, Object>> getAllClaimsFromToken(String token);
    Optional<Date> getExpirationDateFromToken(String token);
    String getSubjectFromToken(String token);
}
