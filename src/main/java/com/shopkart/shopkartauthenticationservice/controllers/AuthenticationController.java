package com.shopkart.shopkartauthenticationservice.controllers;

import com.shopkart.shopkartauthenticationservice.dtos.ResponseStatus;
import com.shopkart.shopkartauthenticationservice.dtos.*;
import com.shopkart.shopkartauthenticationservice.services.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        String ipAddr = httpServletRequest.getHeader("X-Forwarded-For");
        if (ipAddr == null || ipAddr.isEmpty()) {
            ipAddr = httpServletRequest.getRemoteAddr();
        }
        System.out.println("IP Address: " + ipAddr);
        String token = authServiceImpl.login(loginRequestDto.getEmail(), loginRequestDto.getPassword(),ipAddr);
        loginResponseDto.setMessage("Login successful");
        loginResponseDto.setResponseStatus(ResponseStatus.SUCCESS);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", token);
        return new ResponseEntity<>(loginResponseDto, headers, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> doLogout(@RequestHeader(value = "Authorization", required = false) String token) {
        ValidateTokenResponseDto responseDto = new ValidateTokenResponseDto();
        if (token == null || token.isEmpty()) {
            responseDto.setMessage("Invalid token");
            responseDto.setResponseStatus(ResponseStatus.INVALID_CREDENTIALS);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }
        authServiceImpl.logout(token);
        responseDto.setMessage("Logout successful");
        responseDto.setResponseStatus(ResponseStatus.SUCCESS);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> isTokenValid(@RequestHeader(value = "Authorization",required = false) String token) {
        ValidateTokenResponseDto responseDto = new ValidateTokenResponseDto();
        if (token == null || token.isEmpty()) {
            responseDto.setMessage("Invalid token");
            responseDto.setResponseStatus(ResponseStatus.INVALID_CREDENTIALS);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }
        if (authServiceImpl.validateToken(token)) {
            responseDto.setMessage("ok");
            responseDto.setResponseStatus(ResponseStatus.SUCCESS);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }
        responseDto.setMessage("Expired token");
        responseDto.setResponseStatus(ResponseStatus.FAILURE);
        return new ResponseEntity<>(responseDto, HttpStatus.GATEWAY_TIMEOUT);
    }

    @PostMapping("/sign_up")
    public ResponseEntity<?> doSignUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        SignUpResponseDto signUpResponse = new SignUpResponseDto();
        if (authServiceImpl.signUp(signUpRequestDto.getEmail(), signUpRequestDto.getPassword())) {
            signUpResponse.setMessage("Sign up successful");
            signUpResponse.setResponseStatus(ResponseStatus.SUCCESS);
            return new ResponseEntity<>(signUpResponse, HttpStatus.CREATED);
        }
        signUpResponse.setMessage("Something went wrong");
        signUpResponse.setResponseStatus(ResponseStatus.FAILURE);
        return new ResponseEntity<>(signUpResponse, HttpStatus.BAD_REQUEST);
    }
}
