package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.dtos.LoginResponseRecord;
import com.shopkart.shopkartauthenticationservice.dtos.SingUpResponseRecord;
import com.shopkart.shopkartauthenticationservice.dtos.ValidateTokenResponseRecord;

public interface IAuthServices {
    SingUpResponseRecord signup(String email, String password);
    LoginResponseRecord login(String email, String password);
    void logout(String token);
    ValidateTokenResponseRecord validate(String token);
}
