package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.repositories.RoleRepository;
import com.shopkart.shopkartauthenticationservice.repositories.SessionRepository;
import com.shopkart.shopkartauthenticationservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SessionRepository sessionRepository;
    public AuthService(UserRepository userRepository, RoleRepository roleRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sessionRepository = sessionRepository;
    }
}
