package com.shopkart.shopkartauthenticationservice.controllers;

import com.shopkart.shopkartauthenticationservice.dtos.*;
import com.shopkart.shopkartauthenticationservice.dtos.ResponseStatus;
import com.shopkart.shopkartauthenticationservice.exceptions.SessionExpiredException;
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
import org.springframework.web.bind.annotation.*;

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
            headers.add("AUTH_TOKEN", token);
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
    @PostMapping("/logout")
    public ResponseEntity<ValidateTokenResponseDto> doLogout(@RequestHeader(value = "AUTH_TOKEN") String token) {
        ValidateTokenResponseDto responseDto = new ValidateTokenResponseDto();
        if(token==null || token.isEmpty()){
            responseDto.setMessage("Invalid token");
            responseDto.setResponseStatus(ResponseStatus.INVALID_CREDENTIALS);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }
        try {
            if(authService.logout(token)){
                responseDto.setMessage("Logout successful");
                responseDto.setResponseStatus(ResponseStatus.SUCCESS);
                return new ResponseEntity<>(responseDto, HttpStatus.OK);
            }
            responseDto.setMessage("Logout failed");
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }catch (UnAuthorizedException ue){
            responseDto.setMessage(ue.getMessage());
            responseDto.setResponseStatus(ResponseStatus.INVALID_CREDENTIALS);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_GATEWAY);
        }catch (SessionExpiredException se){
            responseDto.setMessage(se.getMessage());
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(responseDto, HttpStatus.GATEWAY_TIMEOUT);
        }catch (RuntimeException re){
            responseDto.setMessage(re.getMessage());
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_GATEWAY);
        }catch (Exception e){
            responseDto.setMessage("Something went wrong");
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_GATEWAY);
        }
    }
    @PostMapping("/validate")
    public ResponseEntity<ValidateTokenResponseDto> isTokenValid(@RequestHeader(value = "AUTH_TOKEN") String token){
        ValidateTokenResponseDto responseDto = new ValidateTokenResponseDto();
        if(token==null || token.isEmpty()){
            responseDto.setMessage("Invalid token");
            responseDto.setResponseStatus(ResponseStatus.INVALID_CREDENTIALS);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }
        try {
            if(authService.validateToken(token)) {
                responseDto.setMessage("ok");
                responseDto.setResponseStatus(ResponseStatus.SUCCESS);
                return new ResponseEntity<>(responseDto, HttpStatus.OK);
            }
            responseDto.setMessage("Invalid token");
            responseDto.setResponseStatus(ResponseStatus.INVALID_CREDENTIALS);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }catch (UnAuthorizedException ue){
            responseDto.setMessage(ue.getMessage());
            responseDto.setResponseStatus(ResponseStatus.INVALID_CREDENTIALS);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_GATEWAY);
        }catch (SessionExpiredException se){
            responseDto.setMessage(se.getMessage());
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(responseDto, HttpStatus.GATEWAY_TIMEOUT);
        }catch (RuntimeException re){
            responseDto.setMessage(re.getMessage());
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_GATEWAY);
        }catch (Exception e){
            responseDto.setMessage("Something went wrong");
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_GATEWAY);
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
