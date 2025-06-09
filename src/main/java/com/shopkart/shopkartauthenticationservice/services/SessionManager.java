package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.models.Session;
import com.shopkart.shopkartauthenticationservice.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Primary
public class SessionManager implements ISessionServices{


    @Override
    public void addSession(User user, String token) {

    }

    @Override
    public void activateSessionByUserId(UUID userId) {

    }

    @Override
    public void deactivateSessionByUserId(UUID sessionId) {

    }

    @Override
    public void saveSession(Session session) {

    }
}
