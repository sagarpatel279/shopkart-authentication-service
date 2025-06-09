package com.shopkart.shopkartauthenticationservice.security.password;

import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Primary
public class BCryptPasswordServiceImpl implements IPasswordService{
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public BCryptPasswordServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder){
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
    }

    @Override
    public String generateSaltPassword(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String encodedPassword, String rawPassword) {
        return bCryptPasswordEncoder.matches(rawPassword,encodedPassword);
    }
}
