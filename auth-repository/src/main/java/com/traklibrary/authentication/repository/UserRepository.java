package com.traklibrary.authentication.repository;

import com.traklibrary.authentication.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailAddress(String emailAddress);

    Collection<User> findByVerifiedIsFalseAndVerificationExpiryDateBefore(LocalDateTime expiryDate);

    Collection<User> findByRecoveryTokenExpiryDateBefore(LocalDateTime expiryDate);
}
