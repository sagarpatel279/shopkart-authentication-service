package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.dtos.LoginResponseRecord;
import com.shopkart.shopkartauthenticationservice.dtos.SingUpResponseRecord;
import com.shopkart.shopkartauthenticationservice.dtos.ValidateTokenResponseRecord;
import com.shopkart.shopkartauthenticationservice.exceptions.*;
import com.shopkart.shopkartauthenticationservice.models.SessionState;
import com.shopkart.shopkartauthenticationservice.models.User;
import com.shopkart.shopkartauthenticationservice.repositories.UserRepository;
import com.shopkart.shopkartauthenticationservice.security.password.IPasswordService;
import com.shopkart.shopkartauthenticationservice.security.token.ITokenService;
import com.shopkart.shopkartauthenticationservice.security.token.TokenState;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Primary
public class AuthServiceImpl implements IAuthServices {
    private final UserRepository userRepository;
    private final ITokenService tokenService;
    private final IPasswordService passwordService;
    private final ISessionServices sessionServices;
    public AuthServiceImpl(UserRepository userRepository, ITokenService tokenService,
                           IPasswordService passwordService, ISessionServices sessionServices) {
        this.userRepository = userRepository;
        this.tokenService=tokenService;
        this.passwordService=passwordService;
        this.sessionServices=sessionServices;
    }

    @Override
    public SingUpResponseRecord signup(String email, String password) {
        boolean isUserExist=userRepository.existsByEmail(email);
        if(isUserExist)
            throw new UserAlreadyExistException("User is already exist with email: "+email);
        User user = new User();
        user.setEmail(email);
        user.setPasswordSalt(passwordService.generateSaltPassword(password));
        user=userRepository.save(user);
        return new SingUpResponseRecord(User.toUserRecord(user));
    }

    @Override
    public LoginResponseRecord login(String email, String password) {
        Optional<User> userOptional=userRepository.findByEmail(email);
        if(userOptional.isEmpty())
            throw new UserNotFoundException("User could not be found by email: "+email);
        User user=userOptional.get();
        if(passwordService.matches(user.getPasswordSalt(),password))
            throw new UnAuthorizedException("Invalid username or password...");
        String token =tokenService.generateToken(User.toClaims(user));
        sessionServices.addSession(user,token);
        return new LoginResponseRecord(token);
    }

    @Override
    public void logout(String token) {
        User user=getUserByValidatingToken(token);
        sessionServices.changeSessionState(user.getUuid(),SessionState.INACTIVE);
    }

    @Override
    public ValidateTokenResponseRecord validate(String token) {
        User user=getUserByValidatingToken(token);
        Optional<SessionState> sessionStateOptional=sessionServices.getSessionStateByUserId(user.getUuid());
        if(sessionStateOptional.isEmpty())
            throw new SessionNotFoundException("No session is found");
        SessionState sessionState=sessionStateOptional.get();

        if(sessionState==SessionState.INACTIVE) {
            throw new InactiveSessionException("User has logged out");
        }else if(sessionState==SessionState.EXPIRED) {
            throw new SessionExpiredException("Login has expired");
        }
        return new ValidateTokenResponseRecord(User.toUserRecord(user));
    }

    private User getUserByValidatingToken(String token){
        TokenState tokenState=tokenService.validateToken(token);
        if(tokenState == TokenState.INVALID_SIGNATURE){
            throw new InvalidTokenException("Invalid login token");
        }
        if(tokenState==TokenState.EXPIRED){
            sessionServices.changeSessionState(UUID.fromString(tokenService.getSubjectFromToken(token)),SessionState.EXPIRED);
            throw new ExpiredTokenException("Login has expired");
        }
        Optional<Map<String,Object>> claimsOptional=tokenService.getAllClaimsFromToken(token);
        if(claimsOptional.isEmpty())
            throw new RuntimeException("Something went wrong to log out");

        Map<String,Object> claims=claimsOptional.get();
        return User.toUser(claims);
    }


}
