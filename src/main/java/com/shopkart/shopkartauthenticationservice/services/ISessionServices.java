package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.models.Session;
import com.shopkart.shopkartauthenticationservice.models.SessionState;

import java.util.UUID;

public interface ISessionServices {
    Session save(Session session);
    void changeSessionState(UUID sessionId, SessionState sessionState);
    SessionState getSessionStateBySessionId(UUID sessionId);
    void deleteBySessionId(UUID uuid);
}
