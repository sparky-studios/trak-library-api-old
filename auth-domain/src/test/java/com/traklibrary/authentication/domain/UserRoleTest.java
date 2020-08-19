package com.traklibrary.authentication.domain;

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
        Assertions.assertThat(result.getVersion()).isNotNull().isGreaterThanOrEqualTo(0L);
    }

    @Test
    void persist_withValidUserRoleRelationships_mapsRelationships() {
        // Arrange
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmailAddress("email@address.com");
        user.setVerified(true);
        user.setVerificationCode("12345");
        user.setVerificationExpiryDate(LocalDateTime.now());
        user.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user.setRecoveryTokenExpiryDate(LocalDateTime.now());
        user = testEntityManager.persistAndFlush(user);

        UserRole userRole = new UserRole();
        userRole.setRole("aaaaaaaaaabbbbbbbbbbcccccccccc");
        userRole.addUser(user);

        // Act
        UserRole result = testEntityManager.persistFlushFind(userRole);

        // Assert
        Assertions.assertThat(result.getUsers().size()).isEqualTo(1);
    }
}
