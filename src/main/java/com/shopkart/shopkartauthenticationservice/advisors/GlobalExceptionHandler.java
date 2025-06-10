package com.shopkart.shopkartauthenticationservice.advisors;

import com.shopkart.shopkartauthenticationservice.dtos.ApiResponseRecord;
import com.shopkart.shopkartauthenticationservice.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnAuthorizedException une) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(une.getMessage());
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<?> handleSessionExpiredException(SessionExpiredException see) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(see.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<?> handleUserAlreadyExistException(UserAlreadyExistException uee) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(uee.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException userNotFound) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userNotFound.getMessage());
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<?> handleSessionNotFoundException(SessionNotFoundException snf) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(snf.getMessage());
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<?> handleExpiredTokenException(ExpiredTokenException ete) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ete.getMessage());
    }

    @ExceptionHandler(InactiveSessionException.class)
    public ResponseEntity<?> handleInactiveSessionException(InactiveSessionException ise) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ise.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException ite) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ite.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: "+ e.getMessage());
    }
}
