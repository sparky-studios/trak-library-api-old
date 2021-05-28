package com.sparkystudios.traklibrary.authentication.domain;

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
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(result.getPassword()).isEqualTo(user.getPassword());
        Assertions.assertThat(result.getEmailAddress()).isEqualTo(user.getEmailAddress());
        Assertions.assertThat(result.isVerified()).isTrue();
        Assertions.assertThat(result.getVerificationCode()).isEqualTo(user.getVerificationCode());
        Assertions.assertThat(result.getVerificationExpiryDate()).isEqualToIgnoringNanos(user.getVerificationExpiryDate());
        Assertions.assertThat(result.getRecoveryToken()).isEqualTo(user.getRecoveryToken());
        Assertions.assertThat(result.getRecoveryTokenExpiryDate()).isEqualToIgnoringNanos(user.getRecoveryTokenExpiryDate());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }

    @Test
    void persist_withValidUserRoleRelationship_mapsRelationship() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setRole("ROLE_USER_ONE");
        userRole = testEntityManager.persistFlushFind(userRole);

        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmailAddress("email@address.com");
        user.setVerified(true);
        user.setUserRole(userRole);

        // Act
        User result = testEntityManager.persistFlushFind(user);

        // Assert
        Assertions.assertThat(result.getUserRole())
                .isEqualTo(userRole);
    }

    @Test
    void persist_withValidRemovedUserRoleRelationships_mapsRelationships() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setRole("ROLE_USER_ONE");
        userRole = testEntityManager.persistFlushFind(userRole);

        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmailAddress("email@address.com");
        user.setVerified(true);
        user.setUserRole(userRole);
        user = testEntityManager.persistFlushFind(user);

        user.setUserRole(null);

        // Act
        User result = testEntityManager.persistFlushFind(user);

        // Assert
        Assertions.assertThat(result.getUserRole()).isNull();
    }

    @Test
    void persist_withValidUserAuthorityRelationships_mapsRelationships() {
        // Arrange
        UserAuthority userAuthority1 = new UserAuthority();
        userAuthority1.setAuthority("test-authority-1");
        userAuthority1.setAuthorityType(AuthorityType.READ);
        userAuthority1.setFeature(Feature.GAMES);
        userAuthority1 = testEntityManager.persistFlushFind(userAuthority1);

        UserAuthority userAuthority2 = new UserAuthority();
        userAuthority2.setAuthority("test-authority-2");
        userAuthority2.setAuthorityType(AuthorityType.READ);
        userAuthority2.setFeature(Feature.GAMES);
        userAuthority2 = testEntityManager.persistFlushFind(userAuthority2);

        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmailAddress("email@address.com");
        user.setVerified(true);
        user.addAuthority(userAuthority1);
        user.addAuthority(userAuthority2);
        user = testEntityManager.persistFlushFind(user);

        // Act
        User result = testEntityManager.persistFlushFind(user);

        // Assert
        Assertions.assertThat(result.getAuthorities().size())
                .isEqualTo(2);
    }

    @Test
    void persist_withValidRemovedGenreRelationships_mapsRelationships() {
        // Arrange
        UserAuthority userAuthority1 = new UserAuthority();
        userAuthority1.setAuthority("test-authority-1");
        userAuthority1.setAuthorityType(AuthorityType.READ);
        userAuthority1.setFeature(Feature.GAMES);
        userAuthority1 = testEntityManager.persistFlushFind(userAuthority1);

        UserAuthority userAuthority2 = new UserAuthority();
        userAuthority2.setAuthority("test-authority-2");
        userAuthority2.setAuthorityType(AuthorityType.READ);
        userAuthority2.setFeature(Feature.GAMES);
        userAuthority2 = testEntityManager.persistFlushFind(userAuthority2);

        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmailAddress("email@address.com");
        user.setVerified(true);
        user.addAuthority(userAuthority1);
        user.addAuthority(userAuthority2);
        user = testEntityManager.persistFlushFind(user);

        user.removeAuthority(testEntityManager.find(UserAuthority.class, userAuthority2.getId()));

        // Act
        User result = testEntityManager.persistFlushFind(user);

        // Assert
        Assertions.assertThat(result.getAuthorities().size()).isEqualTo(1);
        Assertions.assertThat(result.getAuthorities().iterator().next().getId())
                .isEqualTo(userAuthority1.getId());
    }
}
