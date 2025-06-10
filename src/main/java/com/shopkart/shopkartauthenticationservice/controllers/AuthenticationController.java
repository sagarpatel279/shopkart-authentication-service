package com.shopkart.shopkartauthenticationservice.controllers;

import com.shopkart.shopkartauthenticationservice.dtos.*;
import com.shopkart.shopkartauthenticationservice.services.IAuthServices;
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
    private IAuthServices authService;

    @Autowired
    public AuthenticationController(IAuthServices authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseRecord<SignUpResponseRecord>> doSignUp(@RequestBody SignUpRequestRecord signUpRequestDto) {
        SignUpResponseRecord responseRecord= authService.signup(signUpRequestDto.email(),signUpRequestDto.password());

        ApiResponseRecord<SignUpResponseRecord> apiResponseRecord = new ApiResponseRecord<>(responseRecord,
                "User has registered successfully", HttpStatus.CREATED.value());
        return new ResponseEntity<>(apiResponseRecord,HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponseRecord<Void>> doLogin(@RequestBody LoginRequestRecord loginRequestDto) {
        LoginResponseRecord responseRecord=authService.login(loginRequestDto.email(), loginRequestDto.password());
        MultiValueMap<String,String> headers=new LinkedMultiValueMap<>();
        headers.add("AUTH_TOKEN",responseRecord.token());

        ApiResponseRecord<Void> responseApi=new ApiResponseRecord<>(null,
                "User logged in successfully",HttpStatus.OK.value());
        return new ResponseEntity<>(responseApi, headers,HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseRecord<Void>> doLogout(@Nullable @RequestHeader(value = "AUTH_TOKEN") String token) {
        authService.logout(token);

        ApiResponseRecord<Void> responseApi=new ApiResponseRecord<>(null,
                "User logged out successfully",HttpStatus.OK.value());

        return new ResponseEntity<>(responseApi, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponseRecord<ValidateTokenResponseRecord>> validate(@Nullable @RequestHeader(value = "AUTH_TOKEN") String token) {
        ValidateTokenResponseRecord responseRecord=authService.validate(token);

        ApiResponseRecord<ValidateTokenResponseRecord> apiResponseRecord = new ApiResponseRecord<>(responseRecord,
                "Token validated successfully", HttpStatus.CREATED.value());
        return new ResponseEntity<>(apiResponseRecord,HttpStatus.CREATED);
    }

}
