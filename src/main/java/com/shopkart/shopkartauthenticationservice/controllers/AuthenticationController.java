package com.shopkart.shopkartauthenticationservice.controllers;

import com.shopkart.shopkartauthenticationservice.dtos.*;
import com.shopkart.shopkartauthenticationservice.exceptions.UserAlreadyExistException;
import com.shopkart.shopkartauthenticationservice.exceptions.UserNotFoundException;
import com.shopkart.shopkartauthenticationservice.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private AuthService authService;

    @Autowired
    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody LoginRequestDto loginRequestDto) {
        return null;
    }

    @PostMapping("/sign_up")
    public ResponseEntity<SignUpResponseDto> doSignUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        SignUpResponseDto signUpResponse=new SignUpResponseDto();
        try {
            if(authService.signUp(signUpRequestDto.getEmail(),signUpRequestDto.getPassword())){
                signUpResponse.setMessage("Sign up successful");
                signUpResponse.setResponseStatus(ResponseStatus.SUCCESS);
                return new ResponseEntity<SignUpResponseDto>(signUpResponse, HttpStatus.CREATED);
            }
            signUpResponse.setMessage("Something went wrong");
            signUpResponse.setResponseStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<SignUpResponseDto>(signUpResponse, HttpStatus.BAD_REQUEST);
        }catch(UserAlreadyExistException ue){
            signUpResponse.setMessage(ue.getMessage());
            signUpResponse.setResponseStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<SignUpResponseDto>(signUpResponse, HttpStatus.CONFLICT);
        }catch(Exception e){
            signUpResponse.setMessage(e.getMessage());
            signUpResponse.setResponseStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<SignUpResponseDto>(signUpResponse, HttpStatus.BAD_GATEWAY);
        }
    }
}
