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

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private IAuthServices authService;

    @Autowired
    public AuthenticationController(IAuthServices authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponseRecord>> doSignUp(@RequestBody SignUpRequestRecord signUpRequestDto) {
        SignUpResponseRecord responseRecord= authService.signup(signUpRequestDto.email(),signUpRequestDto.password());

        ApiResponse<SignUpResponseRecord> apiResponse = new ApiResponse<>(responseRecord,
                "User has registered successfully", HttpStatus.CREATED.value());
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> doLogin(@RequestBody LoginRequestRecord loginRequestDto) {
        LoginResponseRecord responseRecord=authService.login(loginRequestDto.email(), loginRequestDto.password());
        MultiValueMap<String,String> headers=new LinkedMultiValueMap<>();
        headers.add("AUTH_TOKEN",responseRecord.token());

        ApiResponse<Void> responseApi=new ApiResponse<>(null,
                "User logged in successfully",HttpStatus.OK.value());
        return new ResponseEntity<>(responseApi, headers,HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> doLogout(@Nullable @RequestHeader(value = "AUTH_TOKEN") String token) {
        authService.logout(token);

        ApiResponse<Void> responseApi=new ApiResponse<>(null,
                "User logged out successfully",HttpStatus.OK.value());

        return new ResponseEntity<>(responseApi, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<ValidateTokenResponseRecord>> validate(@Nullable @RequestHeader(value = "AUTH_TOKEN") String token) {
        ValidateTokenResponseRecord responseRecord=authService.validate(token);

        ApiResponse<ValidateTokenResponseRecord> apiResponse = new ApiResponse<>(responseRecord,
                "Token validated successfully", HttpStatus.CREATED.value());
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }

}
