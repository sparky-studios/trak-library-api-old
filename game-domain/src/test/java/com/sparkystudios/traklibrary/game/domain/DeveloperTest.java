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
class DeveloperTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullName_throwsPersistenceException() {
        // Arrange
        Developer developer = new Developer();
        developer.setName(null);
        developer.setDescription("test-description");
        developer.setFoundedDate(LocalDate.now());
        developer.setSlug("test-slug");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(developer));
    }

    @Test
    void persist_withNameExceedingLength_throwsPersistenceException() {
        // Arrange
        Developer developer = new Developer();
        developer.setName(String.join("", Collections.nCopies(300, "t")));
        developer.setDescription("test-description");
        developer.setFoundedDate(LocalDate.now());
        developer.setSlug("test-slug");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(developer));
    }

    @Test
    void persist_withDescriptionExceedingLength_throwsPersistenceException() {
        // Arrange
        Developer developer = new Developer();
        developer.setName("test-name");
        developer.setDescription(String.join("", Collections.nCopies(5000, "t")));
        developer.setFoundedDate(LocalDate.now());
        developer.setSlug("test-slug");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(developer));
    }

    @Test
    void persist_withNullFoundedDate_throwsPersistenceException() {
        // Arrange
        Developer developer = new Developer();
        developer.setName("test-name");
        developer.setDescription("test-description");
        developer.setFoundedDate(null);
        developer.setSlug("test-slug");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(developer));
    }

    @Test
    void persist_withNullSlug_throwsPersistenceException() {
        // Arrange
        Developer developer = new Developer();
        developer.setName("test-name");
        developer.setDescription("test-description");
        developer.setFoundedDate(LocalDate.now());
        developer.setSlug(null);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(developer));
    }

    @Test
    void persist_withSlugExceedingLength_throwsPersistenceException() {
        // Arrange
        Developer developer = new Developer();
        developer.setName("test-name");
        developer.setDescription("test-description");
        developer.setFoundedDate(LocalDate.now());
        developer.setSlug(String.join("", Collections.nCopies(5000, "t")));

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(developer));
    }

    @Test
    void persist_withValidDeveloper_mapsDeveloper() {
        // Arrange
        Developer developer = new Developer();
        developer.setName("test-name");
        developer.setDescription("test-description");
        developer.setFoundedDate(LocalDate.now());
        developer.setSlug("test-slug");

        // Act
        Developer result = testEntityManager.persistFlushFind(developer);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getName()).isEqualTo(developer.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(developer.getDescription());
        Assertions.assertThat(result.getFoundedDate()).isEqualTo(developer.getFoundedDate());
        Assertions.assertThat(result.getSlug()).isEqualTo(developer.getSlug());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }

    @Test
    void persist_withValidAddedGameRelationships_mapsRelationships() {
        // Arrange
        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1.setSlug("test-slug-1");
        game1 = testEntityManager.persistFlushFind(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
        game2.setSlug("test-slug-2");
        game2 = testEntityManager.persistFlushFind(game2);

        Developer developer = new Developer();
        developer.setName("test-name");
        developer.setDescription("test-description");
        developer.setFoundedDate(LocalDate.now());
        developer.setSlug("test-slug");
        developer.addGame(game1);
        developer.addGame(game2);

        // Act
        Developer result = testEntityManager.persistFlushFind(developer);

        // Assert
        Assertions.assertThat(result.getGames().size()).isEqualTo(2);
    }

    @Test
    void persist_withValidRemovedGameRelationships_mapsRelationships() {
        // Arrange
        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1.setSlug("test-slug-1");
        game1 = testEntityManager.persistFlushFind(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
        game2.setSlug("test-slug-2");
        game2 = testEntityManager.persistFlushFind(game2);

        Developer developer = new Developer();
        developer.setName("test-name");
        developer.setDescription("test-description");
        developer.setFoundedDate(LocalDate.now());
        developer.setSlug("test-slug");
        developer.addGame(game1);
        developer.addGame(game2);
        developer = testEntityManager.persistFlushFind(developer);

        developer.removeGame(testEntityManager.find(Game.class, game2.getId()));

        // Act
        Developer result = testEntityManager.persistFlushFind(developer);

        // Assert
        Assertions.assertThat(result.getGames().size()).isEqualTo(1);
        Assertions.assertThat(result.getGames().iterator().next().getId())
                .isEqualTo(game1.getId());
    }
}
