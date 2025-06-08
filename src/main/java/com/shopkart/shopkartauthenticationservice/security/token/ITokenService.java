package com.shopkart.shopkartauthenticationservice.security.token;

import java.util.Date;
import java.util.Map;

public interface ITokenService extends ITokenGenerator,ITokenValidator{
    Map<String,Object> getAllClaimsFromToken(String token);
    String getSubjectFromToken(String token);
    Date getExpirationDateFromToken(String token);
}
