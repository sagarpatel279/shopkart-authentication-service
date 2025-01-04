package com.shopkart.shopkartauthenticationservice.controllers;

import com.shopkart.shopkartauthenticationservice.dtos.*;
import com.shopkart.shopkartauthenticationservice.exceptions.UnAuthorizedException;
import com.shopkart.shopkartauthenticationservice.exceptions.UserAlreadyExistException;
import com.shopkart.shopkartauthenticationservice.exceptions.UserNotFoundException;
import com.shopkart.shopkartauthenticationservice.services.AuthService;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
    public ResponseEntity<LoginResponseDto> doLogin(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        try {
            String token =authService.login(loginRequestDto.getEmail(),loginRequestDto.getPassword());
            loginResponseDto.setMessage("Login successful");
            loginResponseDto.setResponseStatus(ResponseStatus.SUCCESS);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", token);
            return new ResponseEntity<LoginResponseDto>(loginResponseDto,headers, HttpStatus.OK);
        }catch (UnAuthorizedException uae){
            loginResponseDto.setMessage(uae.getMessage());
            loginResponseDto.setResponseStatus(ResponseStatus.INVALID_CREDENTIALS);
            return new ResponseEntity<LoginResponseDto>(loginResponseDto, HttpStatus.UNAUTHORIZED);
        }catch (UserNotFoundException unf){
            loginResponseDto.setMessage(unf.getMessage());
            loginResponseDto.setResponseStatus(ResponseStatus.INVALID_CREDENTIALS);
            return new ResponseEntity<LoginResponseDto>(loginResponseDto, HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            loginResponseDto.setMessage("Something went wrong");
            loginResponseDto.setResponseStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<LoginResponseDto>(loginResponseDto, HttpStatus.BAD_GATEWAY);
        }
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
