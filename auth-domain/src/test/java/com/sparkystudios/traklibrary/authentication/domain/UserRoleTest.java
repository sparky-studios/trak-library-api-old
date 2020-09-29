package com.sparkystudios.traklibrary.authentication.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDateTime;

@DataJpaTest
class UserRoleTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullRole_throwsPersistenceException() {
        // Arrange
        UserRole userRole = new UserRole();

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(userRole));
    }

    @Test
    void persist_withRoleExceedingLength_throwsPersistenceException() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setRole("aaaaaaaaaabbbbbbbbbbccccccccccc");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(userRole));
    }

    @Test
    void persist_withNonUniqueRole_throwsPersistenceException() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setRole("aaaaaaaaaabbbbbbbbbbcccccccccc");

        // Act
        testEntityManager.persistFlushFind(userRole);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(userRole));
    }

    @Test
    void persist_withValidUserRole_mapsUserRole() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setRole("aaaaaaaaaabbbbbbbbbbcccccccccc");

        // Act
        UserRole result = testEntityManager.persistFlushFind(userRole);

        // Assert
        Assertions.assertThat(result.getId()).isGreaterThan(0L);
        Assertions.assertThat(result.getRole()).isEqualTo(userRole.getRole());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isGreaterThanOrEqualTo(0L);
    }

    @Test
    void persist_withValidUserRelationships_mapsRelationships() {
        // Arrange
        User user1 = new User();
        user1.setUsername("username1");
        user1.setPassword("password");
        user1.setEmailAddress("user1@traklibrary.com");
        user1.setVerified(true);
        user1.setVerificationCode("11111");
        user1.setVerificationExpiryDate(LocalDateTime.now());
        user1.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user1.setRecoveryTokenExpiryDate(LocalDateTime.now());
        user1 = testEntityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setUsername("username2");
        user2.setPassword("password");
        user2.setEmailAddress("user2@traklibrary.com");
        user2.setVerified(true);
        user2.setVerificationCode("11111");
        user2.setVerificationExpiryDate(LocalDateTime.now());
        user2.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user2.setRecoveryTokenExpiryDate(LocalDateTime.now());
        user2 = testEntityManager.persistAndFlush(user2);

        UserRole userRole = new UserRole();
        userRole.setRole("aaaaaaaaaabbbbbbbbbbcccccccccc");
        userRole.addUser(user1);
        userRole.addUser(user2);

        // Act
        UserRole result = testEntityManager.persistFlushFind(userRole);

        // Assert
        Assertions.assertThat(result.getUsers().size()).isEqualTo(2);
    }

    @Test
    void persist_withValidRemovedUserRelationships_mapsRelationships() {
        // Arrange
        User user1 = new User();
        user1.setUsername("username1");
        user1.setPassword("password");
        user1.setEmailAddress("user1@traklibrary.com");
        user1.setVerified(true);
        user1.setVerificationCode("11111");
        user1.setVerificationExpiryDate(LocalDateTime.now());
        user1.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user1.setRecoveryTokenExpiryDate(LocalDateTime.now());
        user1 = testEntityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setUsername("username2");
        user2.setPassword("password");
        user2.setEmailAddress("user2@traklibrary.com");
        user2.setVerified(true);
        user2.setVerificationCode("11111");
        user2.setVerificationExpiryDate(LocalDateTime.now());
        user2.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user2.setRecoveryTokenExpiryDate(LocalDateTime.now());
        user2 = testEntityManager.persistAndFlush(user2);

        UserRole userRole = new UserRole();
        userRole.setRole("aaaaaaaaaabbbbbbbbbbcccccccccc");
        userRole.addUser(user1);
        userRole.addUser(user2);
        userRole = testEntityManager.persistFlushFind(userRole);

        userRole.removeUser(testEntityManager.find(User.class, user2.getId()));

        // Act
        UserRole result = testEntityManager.persistFlushFind(userRole);

        // Assert
        Assertions.assertThat(result.getUsers().size()).isEqualTo(1);
        Assertions.assertThat(result.getUsers().iterator().next()).isEqualTo(user1);
    }
}
