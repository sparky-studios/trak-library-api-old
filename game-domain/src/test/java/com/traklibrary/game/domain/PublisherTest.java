package com.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.Collections;

@DataJpaTest
class PublisherTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullName_throwsPersistenceException() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setName(null);
        publisher.setDescription("test-description");
        publisher.setFoundedDate(LocalDate.now());

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(publisher));
    }

    @Test
    void persist_withNameExceedingLength_throwsPersistenceException() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setName(String.join("", Collections.nCopies(300, "t")));
        publisher.setDescription("test-description");
        publisher.setFoundedDate(LocalDate.now());

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(publisher));
    }

    @Test
    void persist_withDescriptionExceedingLength_throwsPersistenceException() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setName("test-name");
        publisher.setDescription(String.join("", Collections.nCopies(5000, "t")));
        publisher.setFoundedDate(LocalDate.now());

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(publisher));
    }

    @Test
    void persist_withNullFoundedDate_throwsPersistenceException() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setName("test-name");
        publisher.setDescription("test-description");
        publisher.setFoundedDate(null);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(publisher));
    }

    @Test
    void persist_withValidPublisher_mapsPublisher() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setName("test-name");
        publisher.setDescription("test-description");
        publisher.setFoundedDate(LocalDate.now());

        // Act
        Publisher result = testEntityManager.persistFlushFind(publisher);

        // Assert
        Assertions.assertThat(result.getId()).isGreaterThan(0L);
        Assertions.assertThat(result.getName()).isEqualTo(publisher.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(publisher.getDescription());
        Assertions.assertThat(result.getFoundedDate()).isEqualTo(publisher.getFoundedDate());
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

        Publisher publisher = new Publisher();
        publisher.setName("test-name");
        publisher.setDescription("test-description");
        publisher.setFoundedDate(LocalDate.now());
        publisher.addGame(game1);
        publisher.addGame(game2);

        // Act
        Publisher result = testEntityManager.persistFlushFind(publisher);

        // Assert
        Assertions.assertThat(result.getGames().size()).isEqualTo(2);
    }
}
