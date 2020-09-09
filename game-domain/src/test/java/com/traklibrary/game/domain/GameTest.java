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
class GameTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullTitle_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle(null);
        game.setDescription("game-description-1");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(game));
    }

    @Test
    void persist_withTitleExceedingLength_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle(String.join("", Collections.nCopies(300, "t")));
        game.setDescription("game-description-1");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(game));
    }

    @Test
    void persist_withDescriptionExceedingLength_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle("test-title");
        game.setDescription(String.join("", Collections.nCopies(5000, "t")));
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(game));
    }

    @Test
    void persist_withNullAgeRating_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(null);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(game));
    }

    @Test
    void persist_withValidGame_mapsGame() {
        // Arrange
        Game game = new Game();
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.MATURE);

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getId()).isGreaterThan(0L);
        Assertions.assertThat(result.getTitle()).isEqualTo(game.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(game.getDescription());
        Assertions.assertThat(result.getReleaseDate()).isEqualTo(game.getReleaseDate());
        Assertions.assertThat(result.getAgeRating()).isEqualTo(game.getAgeRating());
        Assertions.assertThat(result.getVersion()).isNotNull().isGreaterThanOrEqualTo(0L);
    }

    @Test
    void persist_withValidGenreRelationships_mapsRelationships() {
        // Arrange
        Genre genre1 = new Genre();
        genre1.setName("test-name-1");
        genre1.setDescription("test-description-1");
        genre1 = testEntityManager.persistFlushFind(genre1);

        Genre genre2 = new Genre();
        genre2.setName("test-name-2");
        genre2.setDescription("test-description-2");
        genre2 = testEntityManager.persistFlushFind(genre2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.addGenre(genre1);
        game.addGenre(genre2);

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getGenres().size()).isEqualTo(2);
    }

    @Test
    void persist_withValidRemovedGenreRelationships_mapsRelationships() {
        // Arrange
        Genre genre1 = new Genre();
        genre1.setName("test-name-1");
        genre1.setDescription("test-description-1");
        genre1 = testEntityManager.persistFlushFind(genre1);

        Genre genre2 = new Genre();
        genre2.setName("test-name-2");
        genre2.setDescription("test-description-2");
        genre2 = testEntityManager.persistFlushFind(genre2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.addGenre(genre1);
        game.addGenre(genre2);
        game = testEntityManager.persistFlushFind(game);

        game.removeGenre(testEntityManager.find(Genre.class, genre2.getId()));

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getGenres().size()).isEqualTo(1);
        Assertions.assertThat(result.getGenres().iterator().next()).isEqualTo(genre1);
    }

    @Test
    void persist_withValidPlatformRelationships_mapsRelationships() {
        // Arrange
        Platform platform1 = new Platform();
        platform1.setName("test-name-1");
        platform1.setDescription("test-description-1");
        platform1.setReleaseDate(LocalDate.now());
        platform1 = testEntityManager.persistFlushFind(platform1);

        Platform platform2 = new Platform();
        platform2.setName("test-name-2");
        platform2.setDescription("test-description-2");
        platform2.setReleaseDate(LocalDate.now());
        platform2 = testEntityManager.persistFlushFind(platform2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.addPlatform(platform1);
        game.addPlatform(platform2);

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getPlatforms().size()).isEqualTo(2);
    }

    @Test
    void persist_withValidRemovedPlatformRelationships_mapsRelationships() {
        // Arrange
        Platform platform1 = new Platform();
        platform1.setName("test-name-1");
        platform1.setDescription("test-description-1");
        platform1.setReleaseDate(LocalDate.now());
        platform1 = testEntityManager.persistFlushFind(platform1);

        Platform platform2 = new Platform();
        platform2.setName("test-name-2");
        platform2.setDescription("test-description-2");
        platform2.setReleaseDate(LocalDate.now());
        platform2 = testEntityManager.persistFlushFind(platform2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.addPlatform(platform1);
        game.addPlatform(platform2);
        game = testEntityManager.persistFlushFind(game);

        game.removePlatform(testEntityManager.find(Platform.class, platform2.getId()));

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getPlatforms().size()).isEqualTo(1);
        Assertions.assertThat(result.getPlatforms().iterator().next()).isEqualTo(platform1);
    }

    @Test
    void persist_withValidPublishersRelationships_mapsRelationships() {
        // Arrange
        Publisher publisher1 = new Publisher();
        publisher1.setName("test-name-1");
        publisher1.setDescription("test-description-1");
        publisher1.setFoundedDate(LocalDate.now());
        publisher1 = testEntityManager.persistFlushFind(publisher1);

        Publisher publisher2 = new Publisher();
        publisher2.setName("test-name-2");
        publisher2.setDescription("test-description-2");
        publisher2.setFoundedDate(LocalDate.now());
        publisher2 = testEntityManager.persistFlushFind(publisher2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.addPublisher(publisher1);
        game.addPublisher(publisher2);

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getPublishers().size()).isEqualTo(2);
    }

    @Test
    void persist_withValidRemovedPublisherRelationships_mapsRelationships() {
        // Arrange
        Publisher publisher1 = new Publisher();
        publisher1.setName("test-name-1");
        publisher1.setDescription("test-description-1");
        publisher1.setFoundedDate(LocalDate.now());
        publisher1 = testEntityManager.persistFlushFind(publisher1);

        Publisher publisher2 = new Publisher();
        publisher2.setName("test-name-2");
        publisher2.setDescription("test-description-2");
        publisher2.setFoundedDate(LocalDate.now());
        publisher2 = testEntityManager.persistFlushFind(publisher2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.addPublisher(publisher1);
        game.addPublisher(publisher2);
        game = testEntityManager.persistFlushFind(game);

        game.removePublisher(testEntityManager.find(Publisher.class, publisher2.getId()));

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getPublishers().size()).isEqualTo(1);
        Assertions.assertThat(result.getPublishers().iterator().next()).isEqualTo(publisher1);
    }

    @Test
    void persist_withValidDevelopersRelationships_mapsRelationships() {
        // Arrange
        Developer developer1 = new Developer();
        developer1.setName("test-name-1");
        developer1.setDescription("test-description-1");
        developer1.setFoundedDate(LocalDate.now());
        developer1 = testEntityManager.persistFlushFind(developer1);

        Developer developer2 = new Developer();
        developer2.setName("test-name-2");
        developer2.setDescription("test-description-2");
        developer2.setFoundedDate(LocalDate.now());
        developer2 = testEntityManager.persistFlushFind(developer2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.addDeveloper(developer1);
        game.addDeveloper(developer2);

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getDevelopers().size()).isEqualTo(2);
    }

    @Test
    void persist_withValidRemovedDeveloperRelationships_mapsRelationships() {
        // Arrange
        Developer developer1 = new Developer();
        developer1.setName("test-name-1");
        developer1.setDescription("test-description-1");
        developer1.setFoundedDate(LocalDate.now());
        developer1 = testEntityManager.persistFlushFind(developer1);

        Developer developer2 = new Developer();
        developer2.setName("test-name-2");
        developer2.setDescription("test-description-2");
        developer2.setFoundedDate(LocalDate.now());
        developer2 = testEntityManager.persistFlushFind(developer2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.addDeveloper(developer1);
        game.addDeveloper(developer1);
        game = testEntityManager.persistFlushFind(game);

        game.removeDeveloper(testEntityManager.find(Developer.class, developer2.getId()));

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getDevelopers().size()).isEqualTo(1);
        Assertions.assertThat(result.getDevelopers().iterator().next()).isEqualTo(developer1);
    }
}