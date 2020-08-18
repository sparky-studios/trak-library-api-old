package com.traklibrary.authentication.repository;

import com.traklibrary.authentication.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public void beforeAll() {
        userRepository.deleteAll();
    }

    @Test
    void findByUsername_withNonExistentUser_returnsEmptyOptional() {
        // Act
        Optional<User> result = userRepository.findByUsername("username");

        // Assert
        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    void findByUsername_withUser_returnsUser() {
        // Arrange
        User user = new User();
        user.setUsername("username");
        user.setEmailAddress("test@traklibrary.com");
        user.setPassword("password");
        user.setVerified(true);
        user.setVerificationCode("12345");
        user.setVerificationExpiryDate(LocalDateTime.now());
        user.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user.setRecoveryTokenExpiryDate(LocalDateTime.now());

        user = userRepository.save(user);

        // Act
        Optional<User> result = userRepository.findByUsername("username");

        // Assert
        Assertions.assertThat(result).isPresent()
                .isEqualTo(Optional.of(user));
    }

    @Test
    void findByEmailAddress_withNonExistentUser_returnsEmptyOptional() {
        // Act
        Optional<User> result = userRepository.findByEmailAddress("email@address.com");

        // Assert
        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    void findByEmailAddress_withUser_returnsUser() {
        // Arrange
        User user = new User();
        user.setUsername("username");
        user.setEmailAddress("test@traklibrary.com");
        user.setPassword("password");
        user.setVerified(true);
        user.setVerificationCode("12345");
        user.setVerificationExpiryDate(LocalDateTime.now());
        user.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user.setRecoveryTokenExpiryDate(LocalDateTime.now());

        user = userRepository.save(user);

        // Act
        Optional<User> result = userRepository.findByEmailAddress("test@traklibrary.com");

        // Assert
        Assertions.assertThat(result).isPresent()
            .isEqualTo(Optional.of(user));
    }

    @Test
    void findByVerifiedIsFalseAndVerificationExpiryDateBefore_withNoUsers_returnsEmptyCollection() {
        // Act
        Collection<User> result = userRepository.findByVerifiedIsFalseAndVerificationExpiryDateBefore(LocalDateTime.now());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByVerifiedIsFalseAndVerificationExpiryDateBefore_withUsers_returnsUsersWithExpiredVerification() {
        // Arrange
        User user1 = new User();
        user1.setUsername("username");
        user1.setEmailAddress("test@traklibrary.com");
        user1.setPassword("password");
        user1.setVerified(true);
        user1.setVerificationCode("12345");
        user1.setVerificationExpiryDate(LocalDateTime.now().minusDays(1));
        user1.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user1.setRecoveryTokenExpiryDate(LocalDateTime.now());

        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("username2");
        user2.setEmailAddress("test2@traklibrary.com");
        user2.setPassword("password");
        user2.setVerified(false);
        user2.setVerificationCode("12345");
        user2.setVerificationExpiryDate(LocalDateTime.now().minusDays(1));
        user2.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user2.setRecoveryTokenExpiryDate(LocalDateTime.now());

        user2 = userRepository.save(user2);

        // Act
        Collection<User> result = userRepository.findByVerifiedIsFalseAndVerificationExpiryDateBefore(LocalDateTime.now());

        // Assert
        Assertions.assertThat(result).hasSize(1)
            .contains(user2);
    }

    @Test
    void findByRecoveryTokenExpiryDateBefore_withNoUsers_returnsEmptyCollection() {
        // Act
        Collection<User> result = userRepository.findByRecoveryTokenExpiryDateBefore(LocalDateTime.now());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByRecoveryTokenExpiryDateBefore_withUsers_returnsUsersWithExpiredVerification() {
        // Arrange
        User user1 = new User();
        user1.setUsername("username");
        user1.setEmailAddress("test@traklibrary.com");
        user1.setPassword("password");
        user1.setVerified(true);
        user1.setVerificationCode("12345");
        user1.setVerificationExpiryDate(LocalDateTime.now());
        user1.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user1.setRecoveryTokenExpiryDate(LocalDateTime.now().plusDays(1));

        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("username2");
        user2.setEmailAddress("test2@traklibrary.com");
        user2.setPassword("password");
        user2.setVerified(true);
        user2.setVerificationCode("12345");
        user2.setVerificationExpiryDate(LocalDateTime.now());
        user2.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user2.setRecoveryTokenExpiryDate(LocalDateTime.now().minusDays(1));

        user2 = userRepository.save(user2);

        // Act
        Collection<User> result = userRepository.findByRecoveryTokenExpiryDateBefore(LocalDateTime.now());

        // Assert
        Assertions.assertThat(result).hasSize(1)
                .contains(user2);
    }
}

