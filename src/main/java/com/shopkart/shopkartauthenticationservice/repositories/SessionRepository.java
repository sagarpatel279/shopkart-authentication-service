package com.shopkart.shopkartauthenticationservice.repositories;

import com.shopkart.shopkartauthenticationservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {
}
