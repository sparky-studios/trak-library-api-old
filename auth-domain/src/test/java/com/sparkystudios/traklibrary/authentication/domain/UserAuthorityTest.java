package com.sparkystudios.traklibrary.authentication.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.util.Collections;

@DataJpaTest
class UserAuthorityTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullAuthority_throwsPersistenceException() {
        // Arrange
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setAuthority(null);
        userAuthority.setFeature(Feature.GAMES);
        userAuthority.setAuthorityType(AuthorityType.DELETE);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(userAuthority));
    }

    @Test
    void persist_withAuthorityExceedingLength_throwsPersistenceException() {
        // Arrange
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setAuthority(String.join("", Collections.nCopies(300, "t")));
        userAuthority.setFeature(Feature.GAMES);
        userAuthority.setAuthorityType(AuthorityType.DELETE);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(userAuthority));
    }

    @Test
    void persist_withNullFeature_throwsPersistenceException() {
        // Arrange
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setAuthority("test-authority");
        userAuthority.setFeature(null);
        userAuthority.setAuthorityType(AuthorityType.DELETE);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(userAuthority));
    }

    @Test
    void persist_withNullAuthorityType_throwsPersistenceException() {
        // Arrange
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setAuthority("test-authority");
        userAuthority.setFeature(Feature.GAMES);
        userAuthority.setAuthorityType(null);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(userAuthority));
    }

    @Test
    void persist_withValidUserAuthority_mapsUserAuthority() {
        // Arrange
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setAuthority("test-authority");
        userAuthority.setFeature(Feature.GAMES);
        userAuthority.setAuthorityType(AuthorityType.DELETE);

        // Act
        UserAuthority result = testEntityManager.persistFlushFind(userAuthority);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getAuthority()).isEqualTo(userAuthority.getAuthority());
        Assertions.assertThat(result.getFeature()).isEqualTo(userAuthority.getFeature());
        Assertions.assertThat(result.getAuthorityType()).isEqualTo(userAuthority.getAuthorityType());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }

    @Test
    void persist_withValidUserRelationships_mapsRelationships() {
        // Arrange
        User user1 = new User();
        user1.setUsername("username1");
        user1.setPassword("password");
        user1.setEmailAddress("email1@address.com");
        user1.setVerified(true);
        user1 = testEntityManager.persistFlushFind(user1);

        User user2 = new User();
        user2.setUsername("username2");
        user2.setPassword("password");
        user2.setEmailAddress("email2@address.com");
        user2.setVerified(true);
        user2 = testEntityManager.persistFlushFind(user2);

        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setAuthority("test-authority-1");
        userAuthority.setAuthorityType(AuthorityType.READ);
        userAuthority.setFeature(Feature.GAMES);
        userAuthority.addUser(user1);
        userAuthority.addUser(user2);

        // Act
        UserAuthority result = testEntityManager.persistFlushFind(userAuthority);

        // Assert
        Assertions.assertThat(result.getUsers().size())
                .isEqualTo(2);
    }

    @Test
    void persist_withValidRemovedUserRelationships_mapsRelationships() {
        // Arrange
        User user1 = new User();
        user1.setUsername("username1");
        user1.setPassword("password");
        user1.setEmailAddress("email1@address.com");
        user1.setVerified(true);
        user1 = testEntityManager.persistFlushFind(user1);

        User user2 = new User();
        user2.setUsername("username2");
        user2.setPassword("password");
        user2.setEmailAddress("email2@address.com");
        user2.setVerified(true);
        user2 = testEntityManager.persistFlushFind(user2);

        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setAuthority("test-authority-1");
        userAuthority.setAuthorityType(AuthorityType.READ);
        userAuthority.setFeature(Feature.GAMES);
        userAuthority.addUser(user1);
        userAuthority.addUser(user2);
        userAuthority = testEntityManager.persistFlushFind(userAuthority);

        userAuthority.removeUser(testEntityManager.find(User.class, user2.getId()));

        // Act
        UserAuthority result = testEntityManager.persistFlushFind(userAuthority);

        // Assert
        Assertions.assertThat(result.getUsers().size())
                .isEqualTo(1);
        Assertions.assertThat(result.getUsers().iterator().next().getId())
                .isEqualTo(user1.getId());
    }
}
