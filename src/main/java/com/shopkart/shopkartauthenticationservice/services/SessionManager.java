package com.shopkart.shopkartauthenticationservice.services;

import com.shopkart.shopkartauthenticationservice.exceptions.SessionNotFoundException;
import com.shopkart.shopkartauthenticationservice.models.Session;
import com.shopkart.shopkartauthenticationservice.models.SessionState;
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
    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    @Override
    public void changeSessionState(UUID sessionId, SessionState sessionState) {
        Session session=getSessionById(sessionId);
        if(session.getSessionState()!=sessionState){
            session.setSessionState(sessionState);
            sessionRepository.save(session);
        }
    }

    @Override
    public SessionState getSessionStateBySessionId(UUID sessionId) {
        return getSessionById(sessionId).getSessionState();
    }

    @Override
    public void deleteBySessionId(UUID sessionId) {
        boolean isSessionExist= sessionRepository.existsByUuid(sessionId);
        if(isSessionExist)
            sessionRepository.deleteByUuid(sessionId);
    }

    private Session getSessionById(UUID sessionId){
        Optional<Session> sessionOptional= sessionRepository.findByUuidAndIsDeletedIsFalse(sessionId);
        if(sessionOptional.isEmpty())
            throw new SessionNotFoundException("Session could not be found");
        return sessionOptional.get();
    }
}
