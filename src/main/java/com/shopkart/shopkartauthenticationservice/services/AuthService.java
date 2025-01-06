package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.exceptions.SessionExpiredException;
import com.shopkart.shopkartauthenticationservice.exceptions.UnAuthorizedException;
import com.shopkart.shopkartauthenticationservice.exceptions.UserAlreadyExistException;
import com.shopkart.shopkartauthenticationservice.exceptions.UserNotFoundException;
import com.shopkart.shopkartauthenticationservice.models.Role;
import com.shopkart.shopkartauthenticationservice.models.Session;
import com.shopkart.shopkartauthenticationservice.models.User;
import com.shopkart.shopkartauthenticationservice.repositories.RoleRepository;
import com.shopkart.shopkartauthenticationservice.repositories.SessionRepository;
import com.shopkart.shopkartauthenticationservice.repositories.UserRepository;
import io.jsonwebtoken.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SessionRepository sessionRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecretKey key= Jwts.SIG.HS256.key().build();
    public AuthService(UserRepository userRepository, RoleRepository roleRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String login(String email, String password) throws UserNotFoundException, UnAuthorizedException {
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
        String jwsToken = createJWTToken(user.getId(),user.getEmail(),user.getRoles(),session.getId());
        session.setToken(jwsToken);
        sessionRepository.save(session);
        return jwsToken;
    }

    public boolean validateToken(String token) throws SessionExpiredException, UnAuthorizedException {
        Claims claims=validateAndGetClaims(token);
        if(claims!=null){
            Long sessionId= claims.get("sid",Long.class);
            Optional<Session> sessionOptional=sessionRepository.findById(sessionId);
            if(sessionOptional.isEmpty() || sessionOptional.get().getIsDeleted()){
                throw new SessionExpiredException("Session is no longer available");
            }
            return true;
        }
        return false;
    }
    private String createJWTToken(Long userId,String email,List<Role> roles,Long sessionId) {
        Map<String,Object> map = new HashMap<>();
        map.put("uid", userId);
        map.put("email", email);
        map.put("roles", roles);
        map.put("sid", sessionId);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date expiryDate = calendar.getTime();
        return Jwts.builder().claims(map)
                .expiration(expiryDate)
                .issuedAt(new Date()).signWith(key).compact();

    }

    public boolean logout(String token) throws UnAuthorizedException,SessionExpiredException {
        try {
            Claims claims= validateAndGetClaims(token);
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
            return true;
        }catch (Exception e){
            throw new RuntimeException("An error occurred while logging out.");
        }
    }
    private Claims validateAndGetClaims(String token){
        try {
            // Parse the token
            Jws<Claims> jwsClaims = Jwts.parser()
                    .setSigningKey(key) // Ensure 'key' is properly initialized and accessible
                    .build()
                    .parseClaimsJws(token);
            Claims claims=jwsClaims.getBody();
            if(validateTokenExpiration(claims))
                return claims;

            return null; // Token is valid
        } catch (JwtException e) {
            // Handles malformed token, signature errors, etc.
            throw new RuntimeException("Invalid token: " + e.getMessage(), e);
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            throw new RuntimeException("An error occurred while validating the token.", e);
        }
    }
    private boolean validateTokenExpiration(Claims claims) throws SessionExpiredException {
        try {
            // Check for expiration
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                throw new SessionExpiredException("Token has expired.");
            }
            return true;
        }catch (Exception e){
            throw new RuntimeException("An error occurred while checking token expiration.");
        }
    }
    public boolean signUp(String email, String password) throws UserAlreadyExistException {
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
