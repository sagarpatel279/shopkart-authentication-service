package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.models.Session;
import com.shopkart.shopkartauthenticationservice.models.User;

import java.util.UUID;

public interface ISessionServices {
    void addSession(User user, String token);
    void activateSessionByUserId(UUID userId);
    void deactivateSessionByUserId(UUID sessionId);
    void saveSession(Session session);
}
