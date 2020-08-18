package com.traklibrary.authentication.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDateTime;

@DataJpaTest
class UserTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullUsername_throwsPersistenceException() {
        // Arrange
        User user = new User();
        user.setUsername(null);
        user.setEmailAddress("email@address.com");
        user.setPassword("password");
        user.setVerified(true);
        user.setVerificationCode("12345");
        user.setVerificationExpiryDate(LocalDateTime.now());
        user.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user.setRecoveryTokenExpiryDate(LocalDateTime.now());

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(user));
    }

    @Test
    void persist_withNullEmailAddress_throwsPersistenceException() {
        // Arrange
        User user = new User();
        user.setUsername("username");
        user.setEmailAddress(null);
        user.setPassword("password");
        user.setVerified(true);
        user.setVerificationCode("12345");
        user.setVerificationExpiryDate(LocalDateTime.now());
        user.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user.setRecoveryTokenExpiryDate(LocalDateTime.now());

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(user));
    }

    @Test
    void persist_withNullPassword_throwsPersistenceException() {
        // Arrange
        User user = new User();
        user.setUsername("username");
        user.setEmailAddress("email@address.com");
        user.setPassword(null);
        user.setVerified(true);
        user.setVerificationCode("12345");
        user.setVerificationExpiryDate(LocalDateTime.now());
        user.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user.setRecoveryTokenExpiryDate(LocalDateTime.now());

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(user));
    }

    @Test
    void persist_withVerificationCodeExceedingLength_throwsPersistenceException() {
        // Arrange
        User user = new User();
        user.setUsername("username");
        user.setEmailAddress("email@address.com");
        user.setPassword("password");
        user.setVerified(true);
        user.setVerificationCode("123456");
        user.setVerificationExpiryDate(LocalDateTime.now());
        user.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user.setRecoveryTokenExpiryDate(LocalDateTime.now());

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(user));
    }

    @Test
    void persist_withRecoveryTokenExceedingLength_throwsPersistenceException() {
        // Arrange
        User user = new User();
        user.setUsername("username");
        user.setEmailAddress("email@address.com");
        user.setPassword("password");
        user.setVerified(true);
        user.setVerificationCode("12345");
        user.setVerificationExpiryDate(LocalDateTime.now());
        user.setRecoveryToken("aaaaaaaaaabbbbbbbbbbccccccccccc");
        user.setRecoveryTokenExpiryDate(LocalDateTime.now());

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(user));
    }

    @Test
    void persist_withValidUser_mapsUser() {
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

        // Act
        User result = testEntityManager.persistFlushFind(user);

        // Assert
        Assertions.assertThat(result.getId()).isGreaterThan(0L);
        Assertions.assertThat(result.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(result.getPassword()).isEqualTo(user.getPassword());
        Assertions.assertThat(result.getEmailAddress()).isEqualTo(user.getEmailAddress());
        Assertions.assertThat(result.isVerified()).isTrue();
        Assertions.assertThat(result.getVerificationCode()).isEqualTo(user.getVerificationCode());
        Assertions.assertThat(result.getVerificationExpiryDate()).isEqualTo(user.getVerificationExpiryDate());
        Assertions.assertThat(result.getRecoveryToken()).isEqualTo(user.getRecoveryToken());
        Assertions.assertThat(result.getRecoveryTokenExpiryDate()).isEqualTo(user.getRecoveryTokenExpiryDate());
        Assertions.assertThat(result.getVersion()).isNotNull().isGreaterThanOrEqualTo(0L);
    }

    @Test
    void persist_withValidUserRelationships_mapsRelationships() {
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

        UserRole userRole1 = new UserRole();
        userRole1.setRole("ROLE_USER_ONE");
        userRole1 = testEntityManager.persistFlushFind(userRole1);

        UserRole userRole2 = new UserRole();
        userRole2.setRole("ROLE_USER_TWO");
        userRole2 = testEntityManager.persistFlushFind(userRole2);

        User persistedUser = testEntityManager.persistFlushFind(user);

        UserRoleXref userRoleXref1 = new UserRoleXref();
        userRoleXref1.setUserId(persistedUser.getId());
        userRoleXref1.setUserRoleId(userRole1.getId());
        testEntityManager.persistFlushFind(userRoleXref1);

        UserRoleXref userRoleXref2 = new UserRoleXref();
        userRoleXref2.setUserId(persistedUser.getId());
        userRoleXref2.setUserRoleId(userRole2.getId());
        testEntityManager.persistFlushFind(userRoleXref2);

        // Act
        User result = testEntityManager.persistFlushFind(persistedUser);

        // Assert
        Assertions.assertThat(result.getUserRoleXrefs().size()).isEqualTo(2);
        result.getUserRoleXrefs().forEach(userRoleXref -> Assertions.assertThat(userRoleXref.getUserId()).isEqualTo(result.getId()));
    }
}
