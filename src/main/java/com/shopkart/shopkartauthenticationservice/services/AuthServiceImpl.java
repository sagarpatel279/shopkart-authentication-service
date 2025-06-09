package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.dtos.LoginResponseRecord;
import com.shopkart.shopkartauthenticationservice.dtos.SingUpResponseRecord;
import com.shopkart.shopkartauthenticationservice.dtos.UserRecord;
import com.shopkart.shopkartauthenticationservice.dtos.ValidateTokenResponseRecord;
import com.shopkart.shopkartauthenticationservice.exceptions.*;
import com.shopkart.shopkartauthenticationservice.models.Role;
import com.shopkart.shopkartauthenticationservice.models.User;
import com.shopkart.shopkartauthenticationservice.repositories.UserRepository;
import com.shopkart.shopkartauthenticationservice.security.password.IPasswordService;
import com.shopkart.shopkartauthenticationservice.security.token.ITokenService;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
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
        return new SingUpResponseRecord(from(user));
    }

    @Override
    public LoginResponseRecord login(String email, String password) {
        Optional<User> userOptional=userRepository.findByEmail(email);
        if(userOptional.isEmpty())
            throw new UserNotFoundException("User could not be found by email: "+email);
        User user=userOptional.get();
        if(passwordService.matches(user.getPasswordSalt(),password))
            throw new UnAuthorizedException("Invalid username or password...");
        String token =tokenService.generateToken(userToClaims(user));
        sessionServices.addSession(user,token);
        return new LoginResponseRecord(token);
    }

    @Override
    public void logout(String token) {
        if(tokenService.validateToken(token)){
            throw new InvalidTokenException("Invalid or Expired session token, Login again...");
        }
        Optional<Map<String,Object>> claimsOptional=tokenService.getAllClaimsFromToken(token);
        if(claimsOptional.isEmpty())
            throw new RuntimeException("Something went wrong, please login again...");

        Map<String,Object> claims=claimsOptional.get();
        User user=claimsToUser(claims);

        sessionServices.deactivateSessionByUserId(user.getUuid());
    }

    @Override
    public ValidateTokenResponseRecord validate(String token) {
        return null;
    }

    private UserRecord from(User user){
        return new UserRecord(user.getUuid().toString(),user.getEmail(),rolesToString(user.getRoles()));
    }
    private List<String> rolesToString(List<Role> roles){
        return roles.stream().map(Role::getRoleName).toList();
    }

    private List<Role> rolesFromString(List<String> roleNames){
        return roleNames.stream().map(Role::new).toList();
    }
    private Map<String,Object> userToClaims(User user){
        Map<String,Object> claims=new HashMap<>();
        claims.put("sub",user.getUuid());
        claims.put("user",user.getEmail());
        claims.put("roles",rolesToString(user.getRoles()));
        return claims;
    }
    private User claimsToUser(Map<String,Object> claims){
        User user=new User();
        user.setUuid((UUID) claims.get("sub"));
        user.setEmail((String) claims.get("user"));
        user.setRoles(rolesFromString((List<String>) claims.get("roles")));
        return user;
    }
}
