package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.Collections;

@DataJpaTest
class PlatformTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullName_throwsPersistenceException() {
        // Arrange
        Platform platform = new Platform();
        platform.setName(null);
        platform.setDescription("test-description");
        platform.setReleaseDate(LocalDate.now());

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(platform));
    }

    @Test
    void persist_withNameExceedingLength_throwsPersistenceException() {
        // Arrange
        Platform platform = new Platform();
        platform.setName(String.join("", Collections.nCopies(300, "t")));
        platform.setDescription("test-description");
        platform.setReleaseDate(LocalDate.now());

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(platform));
    }

    @Test
    void persist_withDescriptionExceedingLength_throwsPersistenceException() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("test-name");
        platform.setDescription(String.join("", Collections.nCopies(5000, "t")));
        platform.setReleaseDate(LocalDate.now());

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(platform));
    }

    @Test
    void persist_withValidPlatform_mapsPlatform() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("test-name");
        platform.setDescription("test-description");
        platform.setReleaseDate(LocalDate.now());

        // Act
        Platform result = testEntityManager.persistFlushFind(platform);

        // Assert
        Assertions.assertThat(result.getId()).isGreaterThan(0L);
        Assertions.assertThat(result.getName()).isEqualTo(platform.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(platform.getDescription());
        Assertions.assertThat(result.getReleaseDate()).isEqualTo(platform.getReleaseDate());
        Assertions.assertThat(result.getVersion()).isNotNull().isGreaterThanOrEqualTo(0L);
    }

    @Test
    void persist_withValidGameRelationships_mapsRelationships() {
        // Arrange
        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setReleaseDate(LocalDate.now());
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1 = testEntityManager.persistFlushFind(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setReleaseDate(LocalDate.now());
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
        game2 = testEntityManager.persistFlushFind(game2);

        Platform platform = new Platform();
        platform.setName("test-name");
        platform.setDescription("test-description");
        platform.setReleaseDate(LocalDate.now());
        platform.addGame(game1);
        platform.addGame(game2);

        // Act
        Platform result = testEntityManager.persistFlushFind(platform);

        // Assert
        Assertions.assertThat(result.getGames().size()).isEqualTo(2);
    }

    @Test
    void persist_withValidRemovedGameRelationships_mapsRelationships() {
        // Arrange
        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setReleaseDate(LocalDate.now());
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1 = testEntityManager.persistFlushFind(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setReleaseDate(LocalDate.now());
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
        game2 = testEntityManager.persistFlushFind(game2);

        Platform platform = new Platform();
        platform.setName("test-name");
        platform.setDescription("test-description");
        platform.setReleaseDate(LocalDate.now());
        platform.addGame(game1);
        platform.addGame(game2);
        platform = testEntityManager.persistFlushFind(platform);

        platform.removeGame(testEntityManager.find(Game.class, game2.getId()));

        // Act
        Platform result = testEntityManager.persistFlushFind(platform);

        // Assert
        Assertions.assertThat(result.getGames().size()).isEqualTo(1);
        Assertions.assertThat(result.getGames().iterator().next()).isEqualTo(game1);
    }
}
