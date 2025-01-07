package com.shopkart.shopkartauthenticationservice.repositories;

import com.shopkart.shopkartauthenticationservice.models.Session;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SpringJUnitConfig
public class SessionRepositoryTest {
    @Autowired
    private SessionRepository sessionRepository;

    @Test
    void checkQuery(){
//        List<Session> sessions = sessionRepository.findAllActiveSessionsByUserId(1L);
//        System.out.println(sessions.size());
//        List<Session> sessions1= sessionRepository.findAllByUserIdAndIsDeleted(1L,true);
//        System.out.println(sessions1.size());

    }
}