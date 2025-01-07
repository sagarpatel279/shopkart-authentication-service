package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.exceptions.SessionExpiredException;
import com.shopkart.shopkartauthenticationservice.exceptions.UnAuthorizedException;
import com.shopkart.shopkartauthenticationservice.exceptions.UserAlreadyExistException;
import com.shopkart.shopkartauthenticationservice.exceptions.UserNotFoundException;
import com.shopkart.shopkartauthenticationservice.models.Session;
import com.shopkart.shopkartauthenticationservice.models.User;
import com.shopkart.shopkartauthenticationservice.repositories.RoleRepository;
import com.shopkart.shopkartauthenticationservice.repositories.SessionRepository;
import com.shopkart.shopkartauthenticationservice.repositories.UserRepository;
import com.shopkart.shopkartauthenticationservice.utilities.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class AuthService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JwtUtil jwtUtil;
    public AuthService(UserRepository userRepository, RoleRepository roleRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder,JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(String email, String password) {
        Optional<User> userOptional=userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        if(!bCryptPasswordEncoder.matches(password, userOptional.get().getPasswordSalt())){
            throw new UnAuthorizedException("Invalid Password..!");
        }
        User user = userOptional.get();
        Session session=new Session();
        session.setUser(user);
        session =sessionRepository.save(session);
        Map<String,Object> claimsMap=new HashMap<>();
        claimsMap.put("sid",session.getId());
        claimsMap.put("uid",user.getId());
        claimsMap.put("email",user.getEmail());
        claimsMap.put("roles",user.getRoles());
        String jwsToken = jwtUtil.generateToken(claimsMap);
        session.setToken(jwsToken);
        sessionRepository.save(session);
        return jwsToken;
    }

    public void logout(String token) {
        try {
            Claims claims= jwtUtil.getClaimsFromToken(token);
            if(claims==null){
                throw new UnAuthorizedException("Invalid Token");
            }
            Long sessionId=claims.get("sid",Long.class);
            Optional<Session> sessionOptional=sessionRepository.findById(sessionId);
            if(sessionOptional.isEmpty() || sessionOptional.get().getIsDeleted()){
                throw new SessionExpiredException("Session is no longer available");
            }
            Session session=sessionOptional.get();
            session.setIsDeleted(true);
            sessionRepository.save(session);
        }catch (Exception e){
            throw new RuntimeException("An error occurred while logging out.");
        }
    }
    public boolean validateToken(String token){
        return jwtUtil.isTokenValid(token);
    }
    public boolean signUp(String email, String password){
        boolean isUserExist=userRepository.existsByEmail(email);
        if(isUserExist){
            throw new UserAlreadyExistException("User already register");
        }
        User user=new User();
        user.setEmail(email);
        user.setPasswordSalt(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }
}
