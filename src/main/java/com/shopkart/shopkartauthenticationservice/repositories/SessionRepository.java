package com.shopkart.shopkartauthenticationservice.repositories;

import com.shopkart.shopkartauthenticationservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    Optional<Session> findByUuidAndIsDeletedIsFalse(UUID sessionId);

    void deleteByUuid(UUID sessionId);

    boolean existsByUuid(UUID sessionId);
}
