package com.shopkart.shopkartauthenticationservice.advisors;

import com.shopkart.shopkartauthenticationservice.dtos.ErrorResponse;
import com.shopkart.shopkartauthenticationservice.exceptions.SessionExpiredException;
import com.shopkart.shopkartauthenticationservice.exceptions.UnAuthorizedException;
import com.shopkart.shopkartauthenticationservice.exceptions.UserAlreadyExistException;
import com.shopkart.shopkartauthenticationservice.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnAuthorizedException unAuthorizedException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized",unAuthorizedException.getMessage()));
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(SessionExpiredException sessionExpiredException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Session",sessionExpiredException.getMessage()));
    }
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleUserDuplicate(UserAlreadyExistException userAlreadyExistException) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Duplicate",userAlreadyExistException.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException userNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Null",userNotFoundException.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Null",exception.getMessage()));
    }



}
