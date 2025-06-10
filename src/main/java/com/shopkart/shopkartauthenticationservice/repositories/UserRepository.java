package com.shopkart.shopkartauthenticationservice.repositories;

import com.shopkart.shopkartauthenticationservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailAndIsDeletedIsFalse(String email);

    Optional<User> findByEmailAndIsDeletedIsFalse(String email);
}
