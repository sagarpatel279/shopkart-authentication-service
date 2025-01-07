package com.shopkart.shopkartauthenticationservice.services;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import com.shopkart.shopkartauthenticationservice.utilities.UUIDGenerator;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class AuthService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(String email, String password, String ipAddr) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        if (!bCryptPasswordEncoder.matches(password, userOptional.get().getPasswordSalt())) {
            throw new UnAuthorizedException("Invalid Password..!");
        }
        User user = userOptional.get();
        List<Session> sessions = sessionRepository.findAllActiveSessionsByUserId(user.getId());
        if (sessions != null && !sessions.isEmpty()) {
            if (sessions.size() >= 2) {
                System.out.println(sessions.size() + " sessions founds...!");//future
            }
            sessions = sessions.stream().filter(sess -> sess.getIpAddress().equals(ipAddr)).toList();
            if (!sessions.isEmpty()) {
                if (sessions.size() > 1) {
                    for (int i = 1; i < sessions.size(); i++) {
                        Session session = sessions.get(i);
                        session.setIsDeleted(true);
                        sessionRepository.save(session);
                    }
                }
                Session session=sessions.get(0);
                String token=session.getToken();
                if(jwtUtil.isTokenValid(token)){
                    return token;
                }
                throw new SessionExpiredException("Session Expired..!");
            }
        }
        UUID uuid = UUIDGenerator.generateUUID();
        Session session = new Session();
        session.setUser(user);
        session.setIpAddress(ipAddr);
        session.setDeviceId(uuid.toString());
        session = sessionRepository.save(session);
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("sid", session.getId());
        claimsMap.put("uid", user.getId());
        claimsMap.put("did", uuid.toString());
        claimsMap.put("email", user.getEmail());
        claimsMap.put("roles", user.getRoles());
        String jwsToken = jwtUtil.generateToken(claimsMap);
        session.setToken(jwsToken);
        sessionRepository.save(session);
        return jwsToken;
    }

    public void logout(String token) {
        Claims claims = null;
        try {
            claims = jwtUtil.getClaimsFromToken(token);
        } catch (Exception e) {
            throw new UnAuthorizedException("Invalid token");
        }
        if (claims == null) {
            throw new UnAuthorizedException("Invalid Token");
        }
        Long sessionId = claims.get("sid", Long.class);
        Optional<Session> sessionOptional = sessionRepository.findById(sessionId);
        if (sessionOptional.isEmpty() || sessionOptional.get().getIsDeleted()) {
            throw new SessionExpiredException("Session is no longer available");
        }

        Session session = sessionOptional.get();
        session.setIsDeleted(true);
        sessionRepository.save(session);
    }

    public boolean validateToken(String token) {
        if(!jwtUtil.isTokenValid(token)){
            return false;
        }
        Claims claims = null;
        try {
            claims = jwtUtil.getClaimsFromToken(token);
        } catch (Exception e) {
            throw new UnAuthorizedException("Invalid token");
        }
        if (claims == null) {
            throw new UnAuthorizedException("Invalid Token");
        }
        Long sessionId = claims.get("sid", Long.class);
        Optional<Session> sessionOptional = sessionRepository.findById(sessionId);
        if (sessionOptional.isEmpty() || sessionOptional.get().getIsDeleted()) {
            throw new UnAuthorizedException("Session is no longer available");
        }
        return true;
    }

    public boolean signUp(String email, String password) {
        boolean isUserExist = userRepository.existsByEmail(email);
        if (isUserExist) {
            throw new UserAlreadyExistException("User already register");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordSalt(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }
}
