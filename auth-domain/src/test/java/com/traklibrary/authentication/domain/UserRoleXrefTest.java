package com.traklibrary.authentication.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

@DataJpaTest
class UserRoleXrefTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withValidRelationships_mapsRelationships() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setRole("aaaaaaaaaabbbbbbbbbbcccccccccc");
        userRole = testEntityManager.persistFlushFind(userRole);

        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmailAddress("email@address.com");
        user.setVerified(true);
        user.setVerificationCode("12345");
        user.setVerificationExpiryDate(LocalDateTime.now());
        user.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        user.setRecoveryTokenExpiryDate(LocalDateTime.now());

        User persistedUser = testEntityManager.persistFlushFind(user);

        UserRoleXref userRoleXref = new UserRoleXref();
        userRoleXref.setUserId(persistedUser.getId());
        userRoleXref.setUserRoleId(userRole.getId());

        // Act
        UserRoleXref result = testEntityManager.persistFlushFind(userRoleXref);

        // Assert
        Assertions.assertThat(result.getUser()).isNotNull();
        Assertions.assertThat(result.getUser().getId()).isEqualTo(user.getId());
        Assertions.assertThat(result.getUserId()).isEqualTo(user.getId());
        Assertions.assertThat(result.getUserRole()).isNotNull();
        Assertions.assertThat(result.getUserRole().getId()).isEqualTo(userRole.getId());
        Assertions.assertThat(result.getUserRoleId()).isEqualTo(userRole.getId());
    }
}
