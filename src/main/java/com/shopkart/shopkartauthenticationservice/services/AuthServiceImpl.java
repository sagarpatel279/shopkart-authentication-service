package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.dtos.LoginResponseRecord;
import com.shopkart.shopkartauthenticationservice.dtos.SignUpResponseRecord;
import com.shopkart.shopkartauthenticationservice.dtos.ValidateTokenResponseRecord;
import com.shopkart.shopkartauthenticationservice.exceptions.*;
import com.shopkart.shopkartauthenticationservice.models.Session;
import com.shopkart.shopkartauthenticationservice.models.SessionState;
import com.shopkart.shopkartauthenticationservice.models.User;
import com.shopkart.shopkartauthenticationservice.repositories.UserRepository;
import com.shopkart.shopkartauthenticationservice.security.token.JwtProperties;
import com.shopkart.shopkartauthenticationservice.security.token.JwtTokenGenerationService;
import com.shopkart.shopkartauthenticationservice.security.token.JwtTokenParseService;
import com.shopkart.shopkartauthenticationservice.security.token.TokenState;
import com.shopkart.shopkartauthenticationservice.utilities.DateUtility;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Primary
public class AuthServiceImpl implements IAuthServices {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ISessionServices sessionServices;
    private final JwtProperties jwtProperties;
    private JwtTokenGenerationService jwtTokenGenerationService;
    private JwtTokenParseService  jwtTokenParseService;
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           ISessionServices sessionServices, JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
        this.sessionServices=sessionServices;
        this.jwtProperties=jwtProperties;
    }

    @Override
    public SignUpResponseRecord signup(String email, String password) {
        boolean isUserExist=userRepository.existsByEmailAndIsDeletedIsFalse(email);
        if(isUserExist)
            throw new UserAlreadyExistException("User is already exist with email: "+email);
        User user = new User();
        user.setEmail(email);
        user.setPasswordSalt(passwordEncoder.encode(password));
        user=userRepository.save(user);
        return new SignUpResponseRecord(User.toUserRecord(user));
    }

    @Override
    public LoginResponseRecord login(String email, String password) {
        Optional<User> userOptional=userRepository.findByEmailAndIsDeletedIsFalse(email);
        if(userOptional.isEmpty())
            throw new UserNotFoundException("User could not be found by email: "+email);

        User user=userOptional.get();
        if(!passwordEncoder.matches(password,user.getPasswordSalt()))
            throw new UnAuthorizedException("Invalid username or password...");

        Session session=new Session();
        session.setUser(user);
        session.setSessionState(SessionState.ACTIVE);
        session=sessionServices.save(session);
        String token = setJwtTokenGenerationServiceAndGenerateToken(session);
        session.setToken(token);
        sessionServices.save(session);

        return new LoginResponseRecord(token);
    }


    private String setJwtTokenGenerationServiceAndGenerateToken(Session session){
        if(jwtTokenGenerationService==null){
            jwtTokenGenerationService=new JwtTokenGenerationService.Builder()
                    .jwtProperties(jwtProperties)
                    .issuedAt(DateUtility.getCurrentDate())
                    .claims(User.toClaims(session.getUser()))
                    .subject(session.getUser().getEmail())
                    .sessionId(session.getUuid().toString())
                    .build();
        }
        return jwtTokenGenerationService.generateToken();
    }

    @Override
    public void logout(String token) {
        setJwtTokenParseService(token);
        sessionServices.changeSessionState(UUID.fromString(jwtTokenParseService.getSessionId()),SessionState.INACTIVE);
    }

    @Override
    public ValidateTokenResponseRecord validate(String token) {
        User user=getUserByValidatingToken(token);
        SessionState sessionState=sessionServices.getSessionStateBySessionId(UUID.fromString(jwtTokenParseService.getSessionId()));

        if(sessionState==SessionState.INACTIVE) {
            throw new InactiveSessionException("User has logged out");
        }else if(sessionState==SessionState.EXPIRED) {
            throw new SessionExpiredException("Login has expired");
        }else if(sessionState!=SessionState.ACTIVE) {
            throw new SessionExpiredException("Session is not active");
        }
        return new ValidateTokenResponseRecord(User.toUserRecord(user));
    }

    private void setJwtTokenParseService(String token){
        jwtTokenParseService= new JwtTokenParseService.Builder()
                .jwtProperties(jwtProperties)
                .token(token)
                .build();
    }

    private User getUserByValidatingToken(String token){
        setJwtTokenParseService(token);

        TokenState tokenState=jwtTokenParseService.getTokenState();

        if(tokenState == TokenState.INVALID_SIGNATURE){
            throw new InvalidTokenException("Invalid login token");
        }else if(tokenState==TokenState.EXPIRED){
            sessionServices.changeSessionState(UUID.fromString(jwtTokenParseService.getSessionId()),SessionState.EXPIRED);
            throw new ExpiredTokenException("Login has expired");
        }
        Optional<Map<String,Object>> claimsOptional=jwtTokenParseService.getAllClaimsFromToken();

        if(claimsOptional.isEmpty())
            throw new RuntimeException("Something went wrong to log out");

        Map<String,Object> claims=claimsOptional.get();
        return User.toUser(claims);
    }


}
