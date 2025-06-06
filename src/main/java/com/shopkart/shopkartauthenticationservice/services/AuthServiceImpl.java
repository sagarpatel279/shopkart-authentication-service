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
import com.shopkart.shopkartauthenticationservice.utilities.UUIDGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;


@Service
public class AuthServiceImpl implements IAuthServices {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SessionRepository sessionRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Value("${jwt.token.key}")
    private String SECRET_KEY;
    @Value("${jwt.token.expiration.time}")
    private long EXPIRATION_TIME;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String login(String email, String password, String ipAddr) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        if (!bCryptPasswordEncoder.matches(password, userOptional.get().getPasswordSalt())) {
            throw new UnAuthorizedException("Invalid Password..!");
        }
        User user = userOptional.get();
        List<Session> sessions = sessionRepository.findAllActiveSessionsByUserId(user.getUuid());
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
                Session session = sessions.get(0);
                String token = session.getToken();
                if (isTokenValid(token)) {
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
        claimsMap.put("sid", session.getUuid());
        claimsMap.put("uid", user.getUuid());
        claimsMap.put("did", uuid.toString());
        claimsMap.put("email", user.getEmail());
        claimsMap.put("roles", user.getRoles());
        String jwsToken = generateToken(claimsMap);
        session.setToken(jwsToken);
        sessionRepository.save(session);
        return jwsToken;
    }

    @Override
    public void logout(String token) {
        Claims claims = null;
        try {
            claims = getClaimsFromToken(token);
        } catch (ExpiredJwtException jwe) {
            UUID sessionId = jwe.getClaims().get("sid", UUID.class);
            if (deleteTokenFromDb(sessionId))
                throw new SessionExpiredException("Session Expired..!");
            throw new UnAuthorizedException("Invalid token");
        } catch (JwtException je) {
            throw new UnAuthorizedException("Invalid token");
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong!");
        }
        if (claims == null) {
            throw new UnAuthorizedException("Invalid Token");
        }
        UUID sessionId = claims.get("sid", UUID.class);
        Optional<Session> sessionOptional = sessionRepository.findById(sessionId);
        if (sessionOptional.isEmpty() || sessionOptional.get().getIsDeleted()) {
            throw new SessionExpiredException("Session is no longer available");
        }

        Session session = sessionOptional.get();
        session.setIsDeleted(true);
        sessionRepository.save(session);
    }

    @Override
    public boolean validateToken(String token) {
        if (!isTokenValid(token)) {
            return false;
        }
        Claims claims = null;
        try {
            claims = getClaimsFromToken(token);
        }catch (ExpiredJwtException jwe) {
            UUID sessionId = jwe.getClaims().get("sid", UUID.class);
            if (deleteTokenFromDb(sessionId))
                throw new SessionExpiredException("Session Expired..!");
            throw new UnAuthorizedException("Invalid token");
        } catch (JwtException je) {
            throw new UnAuthorizedException("Invalid token");
        } catch (Exception e) {
            throw new RuntimeException("Something went Wrong..!");
        }
        if (claims == null) {
            throw new UnAuthorizedException("Invalid Token");
        }
        UUID sessionId = claims.get("sid", UUID.class);
        Optional<Session> sessionOptional = sessionRepository.findById(sessionId);
        if (sessionOptional.isEmpty() || sessionOptional.get().getIsDeleted()) {
            throw new UnAuthorizedException("Session is no longer available");
        }
        return true;
    }

    @Override
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

    public boolean deleteTokenFromDb(UUID sessionId) {
        Optional<Session> sessionOptional = sessionRepository.findById(sessionId);
        if (sessionOptional.isPresent() && !sessionOptional.get().getIsDeleted()) {
            Session session = sessionOptional.get();
            session.setIsDeleted(true);
            sessionRepository.save(session);
            return true;
        }
        return false;
    }
    private String generateToken(Map<String, ?> claimsMap) {
        return Jwts.builder().claims(claimsMap)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .issuedAt(new Date())
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenValid(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date(System.currentTimeMillis()));
        } catch (ExpiredJwtException eje) {
            UUID sessionId = eje.getClaims().get("sid", UUID.class);
            if (deleteTokenFromDb(sessionId))
                throw new SessionExpiredException("Token Expired-Utility");
            throw new UnAuthorizedException("Invalid Token - Utility-2");
        } catch (JwtException je) {
            throw new UnAuthorizedException("Invalid Token-Utility");
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong-Utility");
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
