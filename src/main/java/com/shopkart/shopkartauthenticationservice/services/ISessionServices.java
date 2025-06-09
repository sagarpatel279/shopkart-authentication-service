package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.models.Session;
import com.shopkart.shopkartauthenticationservice.models.SessionState;
import com.shopkart.shopkartauthenticationservice.models.User;

import java.util.Optional;
import java.util.UUID;

public interface ISessionServices {
    void addSession(User user, String token);
    void changeSessionState(UUID userId,SessionState sessionState);
    Optional<SessionState> getSessionStateByUserId(UUID userId);
}
