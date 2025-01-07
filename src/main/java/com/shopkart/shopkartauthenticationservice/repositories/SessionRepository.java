package com.shopkart.shopkartauthenticationservice.repositories;

import com.shopkart.shopkartauthenticationservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {

    @Query("SELECT s FROM sessions s WHERE s.user.id=:userId and s.isDeleted=false")
    List<Session> findAllActiveSessionsByUserId(@Param("userId") long userId);

}
