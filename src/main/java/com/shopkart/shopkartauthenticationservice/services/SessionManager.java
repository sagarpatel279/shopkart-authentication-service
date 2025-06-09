package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.models.Session;
import com.shopkart.shopkartauthenticationservice.models.SessionState;
import com.shopkart.shopkartauthenticationservice.models.User;
import com.shopkart.shopkartauthenticationservice.repositories.SessionRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Primary
public class SessionManager implements ISessionServices{

    private SessionRepository sessionRepository;

    public SessionManager(SessionRepository sessionRepository){
        this.sessionRepository=sessionRepository;
    }

    @Override
    public void addSession(User user, String token) {
        Session session=new Session();
        session.setUser(user);
        session.setToken(token);
        session.setSessionState(SessionState.ACTIVE);
        sessionRepository.save(session);
    }

    @Override
    public void changeSessionState(UUID userId, SessionState sessionState) {
//        Optional<Session> sessionOptional=
    }

    @Override
    public Optional<SessionState> getSessionStateByUserId(UUID userId) {
        return null;
    }

}
