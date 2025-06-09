package com.shopkart.shopkartauthenticationservice.advisors;

import com.shopkart.shopkartauthenticationservice.dtos.ApiResponse;
import com.shopkart.shopkartauthenticationservice.exceptions.SessionExpiredException;
import com.shopkart.shopkartauthenticationservice.exceptions.UnAuthorizedException;
import com.shopkart.shopkartauthenticationservice.exceptions.UserAlreadyExistException;
import com.shopkart.shopkartauthenticationservice.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnAuthorizedException une) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("UnAuthorized",une.getMessage(), ResponseStatus.INVALID_CREDENTIALS));
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<?> handleSessionExpiredException(SessionExpiredException see) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>("Expired Session",see.getMessage(),ResponseStatus.FAILURE));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<?> handleUserAlreadyExistException(UserAlreadyExistException uee) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("Data Exist",uee.getMessage(),ResponseStatus.FAILURE));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException userNotFound) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Not Found",userNotFound.getMessage(),ResponseStatus.FAILURE));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse<>("Bad Gateway","Something Went Wrong",ResponseStatus.FAILURE));
    }
}
