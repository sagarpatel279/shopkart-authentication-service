package com.shopkart.shopkartauthenticationservice.controllers;

import com.shopkart.shopkartauthenticationservice.dtos.LoginRequestRecord;
import com.shopkart.shopkartauthenticationservice.dtos.SignUpRequestRecord;
import com.shopkart.shopkartauthenticationservice.services.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private AuthServiceImpl authServiceImpl;

    @Autowired
    public AuthenticationController(AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> doSignUp(@RequestBody SignUpRequestRecord signUpRequestDto) {
        return null;
    }
    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody LoginRequestRecord loginRequestDto) {
        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> doLogout(@Nullable @RequestHeader(value = "AUTH_TOKEN") String token) {
        return null;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@Nullable @RequestHeader(value = "AUTH_TOKEN") String token) {
        return null;
    }

}
