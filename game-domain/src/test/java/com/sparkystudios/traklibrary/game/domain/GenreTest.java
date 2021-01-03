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
class GenreTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullName_throwsPersistenceException() {
        // Arrange
        Genre genre = new Genre();
        genre.setName(null);
        genre.setDescription("test-description");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(genre));
    }

    @Test
    void persist_withNameExceedingLength_throwsPersistenceException() {
        // Arrange
        Genre genre = new Genre();
        genre.setName(String.join("", Collections.nCopies(300, "t")));
        genre.setDescription("test-description");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(genre));
    }

    @Test
    void persist_withValidGenre_mapsGenre() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("test-name");
        genre.setDescription("test-description");

        // Act
        Genre result = testEntityManager.persistFlushFind(genre);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getName()).isEqualTo(genre.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(genre.getDescription());
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }

    @Test
    void persist_withValidGameRelationships_mapsRelationships() {
        // Arrange
        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1 = testEntityManager.persistFlushFind(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
        game2 = testEntityManager.persistFlushFind(game2);

        Genre genre = new Genre();
        genre.setName("test-name");
        genre.setDescription("test-description");
        genre.addGame(game1);
        genre.addGame(game2);

        // Act
        Genre result = testEntityManager.persistFlushFind(genre);

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
        game1 = testEntityManager.persistFlushFind(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
        game2 = testEntityManager.persistFlushFind(game2);

        Genre genre = new Genre();
        genre.setName("test-name");
        genre.setDescription("test-description");
        genre.addGame(game1);
        genre.addGame(game2);
        genre = testEntityManager.persistFlushFind(genre);

        genre.removeGame(testEntityManager.find(Game.class, game2.getId()));

        // Act
        Genre result = testEntityManager.persistFlushFind(genre);

        // Assert
        Assertions.assertThat(result.getGames().size()).isEqualTo(1);
        Assertions.assertThat(result.getGames().iterator().next().getId())
                .isEqualTo(game1.getId());
    }
}
