package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
class FranchiseTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullTitle_throwsPersistenceException() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setTitle(null);
        franchise.setDescription("test-description");
        franchise.setSlug("test-slug");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(franchise));
    }

    @Test
    void persist_withTitleExceedingLength_throwsPersistenceException() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setTitle(String.join("", Collections.nCopies(5000, "t")));
        franchise.setDescription("test-description");
        franchise.setSlug("test-slug");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(franchise));
    }

    @Test
    void persist_withDescriptionExceedingLength_throwsPersistenceException() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setTitle("test-title");
        franchise.setDescription(String.join("", Collections.nCopies(5000, "t")));
        franchise.setSlug("test-slug");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(franchise));
    }

    @Test
    void persist_withNullSlug_throwsPersistenceException() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setTitle("test-title");
        franchise.setDescription("test-description");
        franchise.setSlug(null);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(franchise));
    }

    @Test
    void persist_withSlugExceedingLength_throwsPersistenceException() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setTitle("test-title");
        franchise.setDescription("test-description");
        franchise.setSlug(String.join("", Collections.nCopies(5000, "t")));

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(franchise));
    }

    @Test
    void persist_withValidFranchise_mapsFranchise() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setTitle("test-title");
        franchise.setDescription("test-description");
        franchise.setSlug("test-slug");

        // Act
        Franchise result = testEntityManager.persistFlushFind(franchise);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getTitle()).isEqualTo(franchise.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(franchise.getDescription());
        Assertions.assertThat(result.getSlug()).isEqualTo(franchise.getSlug());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }

    @Test
    void persist_withValidGameRelationships_mapsRelationships() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setTitle("test-title");
        franchise.setDescription("test-description");
        franchise.setSlug("test-slug");
        franchise = testEntityManager.persistFlushFind(franchise);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1.setFranchiseId(franchise.getId());
        game1.setSlug("test-slug-1");
        game1 = testEntityManager.persistFlushFind(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
        game2.setFranchiseId(franchise.getId());
        game2.setSlug("test-slug-2");
        game2 = testEntityManager.persistFlushFind(game2);

        // Act
        Franchise result = testEntityManager.persistFlushFind(franchise);

        // Assert
        Assertions.assertThat(result.getGames()).hasSize(2);
        Assertions.assertThat(result.getGames().stream().map(Game::getId).collect(Collectors.toList()))
                .isEqualTo(List.of(game1.getId(), game2.getId()));
    }
}
