package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        game.setSlug("test-slug");

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
        game.setSlug("test-slug");

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
        game.setSlug("test-slug");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(game));
    }

    @Test
    void persist_withNullSlug_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle("test-title");
        game.setDescription("game-description-1");
        game.setSlug(null);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(game));
    }

    @Test
    void persist_withSlugExceedingLength_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle("test-title");
        game.setDescription("game-description-1");
        game.setSlug(String.join("", Collections.nCopies(300, "t")));

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
        game.setSlug("test-slug");
        game.getGameModes().add(GameMode.SINGLE_PLAYER);
        game.getGameModes().add(GameMode.MULTI_PLAYER);

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getTitle()).isEqualTo(game.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(game.getDescription());
        Assertions.assertThat(result.getGameModes()).isEqualTo(game.getGameModes());
        Assertions.assertThat(result.getSlug()).isEqualTo(game.getSlug());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }

    @Test
    void persist_withValidAgeRatingRelationships_mapsRelationships() {
        // Arrange
        AgeRating ageRating1 = new AgeRating();
        ageRating1.setClassification(AgeRatingClassification.ESRB);
        ageRating1.setRating((short)2);

        AgeRating ageRating2 = new AgeRating();
        ageRating2.setClassification(AgeRatingClassification.CERO);
        ageRating2.setRating((short)4);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
        game.addAgeRating(ageRating1);
        game.addAgeRating(ageRating2);

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getAgeRatings().size()).isEqualTo(2);
    }

    @Test
    void persist_withValidRemovedAgeRatingRelationships_mapsRelationships() {
        // Arrange
        AgeRating ageRating1 = new AgeRating();
        ageRating1.setClassification(AgeRatingClassification.ESRB);
        ageRating1.setRating((short)2);

        AgeRating ageRating2 = new AgeRating();
        ageRating2.setClassification(AgeRatingClassification.CERO);
        ageRating2.setRating((short)4);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
        game.addAgeRating(ageRating1);
        game.addAgeRating(ageRating2);
        game = testEntityManager.persistFlushFind(game);

        game.removeAgeRating(testEntityManager.find(AgeRating.class, game.getAgeRatings().iterator().next().getId()));

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getAgeRatings().size()).isEqualTo(1);
    }

    @Test
    void persist_withValidGenreRelationships_mapsRelationships() {
        // Arrange
        Genre genre1 = new Genre();
        genre1.setName("test-name-1");
        genre1.setDescription("test-description-1");
        genre1.setSlug("test-slug-1");
        genre1 = testEntityManager.persistFlushFind(genre1);

        Genre genre2 = new Genre();
        genre2.setName("test-name-2");
        genre2.setDescription("test-description-2");
        genre2.setSlug("test-slug-2");
        genre2 = testEntityManager.persistFlushFind(genre2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
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
        genre1.setSlug("test-slug-1");
        genre1 = testEntityManager.persistFlushFind(genre1);

        Genre genre2 = new Genre();
        genre2.setName("test-name-2");
        genre2.setDescription("test-description-2");
        genre2.setSlug("test-slug-2");
        genre2 = testEntityManager.persistFlushFind(genre2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
        game.addGenre(genre1);
        game.addGenre(genre2);
        game = testEntityManager.persistFlushFind(game);

        game.removeGenre(testEntityManager.find(Genre.class, genre2.getId()));

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getGenres().size()).isEqualTo(1);
        Assertions.assertThat(result.getGenres().iterator().next().getId())
                .isEqualTo(genre1.getId());
    }

    @Test
    void persist_withValidPlatformRelationships_mapsRelationships() {
        // Arrange
        Platform platform1 = new Platform();
        platform1.setName("test-name-1");
        platform1.setDescription("test-description-1");
        platform1.setSlug("test-slug-1");
        platform1 = testEntityManager.persistFlushFind(platform1);

        Platform platform2 = new Platform();
        platform2.setName("test-name-2");
        platform2.setDescription("test-description-2");
        platform2.setSlug("test-slug-2");
        platform2 = testEntityManager.persistFlushFind(platform2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
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
        platform1.setSlug("test-slug-1");
        platform1 = testEntityManager.persistFlushFind(platform1);

        Platform platform2 = new Platform();
        platform2.setName("test-name-2");
        platform2.setDescription("test-description-2");
        platform2.setSlug("test-slug-2");
        platform2 = testEntityManager.persistFlushFind(platform2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
        game.addPlatform(platform1);
        game.addPlatform(platform2);
        game = testEntityManager.persistFlushFind(game);

        game.removePlatform(testEntityManager.find(Platform.class, platform2.getId()));

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getPlatforms().size()).isEqualTo(1);
        Assertions.assertThat(result.getPlatforms().iterator().next().getId())
                .isEqualTo(platform1.getId());
    }

    @Test
    void persist_withValidPublishersRelationships_mapsRelationships() {
        // Arrange
        Publisher publisher1 = new Publisher();
        publisher1.setName("test-name-1");
        publisher1.setDescription("test-description-1");
        publisher1.setFoundedDate(LocalDate.now());
        publisher1.setSlug("test-slug-1");
        publisher1 = testEntityManager.persistFlushFind(publisher1);

        Publisher publisher2 = new Publisher();
        publisher2.setName("test-name-2");
        publisher2.setDescription("test-description-2");
        publisher2.setFoundedDate(LocalDate.now());
        publisher2.setSlug("test-slug-2");
        publisher2 = testEntityManager.persistFlushFind(publisher2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
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
        publisher1.setSlug("test-slug-1");
        publisher1 = testEntityManager.persistFlushFind(publisher1);

        Publisher publisher2 = new Publisher();
        publisher2.setName("test-name-2");
        publisher2.setDescription("test-description-2");
        publisher2.setFoundedDate(LocalDate.now());
        publisher2.setSlug("test-slug-2");
        publisher2 = testEntityManager.persistFlushFind(publisher2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
        game.addPublisher(publisher1);
        game.addPublisher(publisher2);
        game = testEntityManager.persistFlushFind(game);

        game.removePublisher(testEntityManager.find(Publisher.class, publisher2.getId()));

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getPublishers().size()).isEqualTo(1);
        Assertions.assertThat(result.getPublishers().iterator().next().getId())
                .isEqualTo(publisher1.getId());
    }

    @Test
    void persist_withValidDevelopersRelationships_mapsRelationships() {
        // Arrange
        Developer developer1 = new Developer();
        developer1.setName("test-name-1");
        developer1.setDescription("test-description-1");
        developer1.setFoundedDate(LocalDate.now());
        developer1.setSlug("test-slug-1");
        developer1 = testEntityManager.persistFlushFind(developer1);

        Developer developer2 = new Developer();
        developer2.setName("test-name-2");
        developer2.setDescription("test-description-2");
        developer2.setFoundedDate(LocalDate.now());
        developer2.setSlug("test-slug-2");
        developer2 = testEntityManager.persistFlushFind(developer2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
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
        developer1.setSlug("test-slug-1");
        developer1 = testEntityManager.persistFlushFind(developer1);

        Developer developer2 = new Developer();
        developer2.setName("test-name-2");
        developer2.setDescription("test-description-2");
        developer2.setFoundedDate(LocalDate.now());
        developer2.setSlug("test-slug-2");
        developer2 = testEntityManager.persistFlushFind(developer2);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
        game.addDeveloper(developer1);
        game.addDeveloper(developer1);
        game = testEntityManager.persistFlushFind(game);

        game.removeDeveloper(testEntityManager.find(Developer.class, developer2.getId()));

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getDevelopers().size()).isEqualTo(1);
        Assertions.assertThat(result.getDevelopers().iterator().next().getId())
                .isEqualTo(developer1.getId());
    }

    @Test
    void persist_withValidGameReleaseDateRelationships_mapsRelationships() {
        // Arrange
        GameReleaseDate gameReleaseDate1 = new GameReleaseDate();
        gameReleaseDate1.setRegion(GameRegion.NORTH_AMERICA);
        gameReleaseDate1.setReleaseDate(LocalDate.now());
        gameReleaseDate1 = testEntityManager.persistAndFlush(gameReleaseDate1);

        GameReleaseDate gameReleaseDate2 = new GameReleaseDate();
        gameReleaseDate2.setRegion(GameRegion.PAL);
        gameReleaseDate2.setReleaseDate(LocalDate.now());
        gameReleaseDate2 = testEntityManager.persistAndFlush(gameReleaseDate2);

        GameReleaseDate gameReleaseDate3 = new GameReleaseDate();
        gameReleaseDate3.setRegion(GameRegion.JAPAN);
        gameReleaseDate3.setReleaseDate(LocalDate.now());
        gameReleaseDate3 = testEntityManager.persistAndFlush(gameReleaseDate3);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
        game.addReleaseDate(gameReleaseDate1);
        game.addReleaseDate(gameReleaseDate2);
        game.addReleaseDate(gameReleaseDate3);

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getReleaseDates().size()).isEqualTo(3);
        Assertions.assertThat(result.getReleaseDates().stream().map(GameReleaseDate::getId).sorted().collect(Collectors.toList()))
                .isEqualTo(List.of(gameReleaseDate1.getId(), gameReleaseDate2.getId(), gameReleaseDate3.getId()));
    }

    @Test
    void persist_withValidRemovedGameReleaseDateRelationships_mapsRelationships() {
        // Arrange
        GameReleaseDate gameReleaseDate1 = new GameReleaseDate();
        gameReleaseDate1.setRegion(GameRegion.NORTH_AMERICA);
        gameReleaseDate1.setReleaseDate(LocalDate.now());

        GameReleaseDate gameReleaseDate2 = new GameReleaseDate();
        gameReleaseDate2.setRegion(GameRegion.PAL);
        gameReleaseDate2.setReleaseDate(LocalDate.now());

        GameReleaseDate gameReleaseDate3 = new GameReleaseDate();
        gameReleaseDate3.setRegion(GameRegion.JAPAN);
        gameReleaseDate3.setReleaseDate(LocalDate.now());

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
        game.addReleaseDate(gameReleaseDate1);
        game.addReleaseDate(gameReleaseDate2);
        game.addReleaseDate(gameReleaseDate3);
        game = testEntityManager.persistFlushFind(game);

        game.removeReleaseDate(testEntityManager.find(GameReleaseDate.class, gameReleaseDate1.getId()));
        game.removeReleaseDate(testEntityManager.find(GameReleaseDate.class, gameReleaseDate2.getId()));

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getReleaseDates().size()).isEqualTo(1);
        Assertions.assertThat(result.getReleaseDates().iterator().next().getId())
                .isEqualTo(gameReleaseDate3.getId());
    }

    @Test
    void persist_withValidDownloadableContentsRelationships_mapsRelationships() {
        // Arrange
        DownloadableContent downloadableContent1 = new DownloadableContent();
        downloadableContent1.setName("test-name-1");
        downloadableContent1.setDescription("test-description-1");
        downloadableContent1.setSlug("test-slug-1");
        downloadableContent1.setReleaseDate(LocalDate.now());

        DownloadableContent downloadableContent2 = new DownloadableContent();
        downloadableContent2.setName("test-name-2");
        downloadableContent2.setDescription("test-description-2");
        downloadableContent2.setSlug("test-slug-2");
        downloadableContent2.setReleaseDate(LocalDate.now());

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setSlug("test-slug");
        game.addDownloadableContent(downloadableContent1);
        game.addDownloadableContent(downloadableContent2);
        game = testEntityManager.persistFlushFind(game);

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getDownloadableContents().size()).isEqualTo(2);
        Assertions.assertThat(result.getDownloadableContents().stream().map(DownloadableContent::getId).sorted().collect(Collectors.toList()))
                .isEqualTo(List.of(downloadableContent1.getId(), downloadableContent2.getId()));
    }

    @Test
    void persist_withValidRemovedDownloadableContentsRelationships_mapsRelationships() {
        // Arrange
        DownloadableContent downloadableContent1 = new DownloadableContent();
        downloadableContent1.setName("test-name-1");
        downloadableContent1.setDescription("test-description-1");
        downloadableContent1.setSlug("test-slug-1");
        downloadableContent1.setReleaseDate(LocalDate.now());

        DownloadableContent downloadableContent2 = new DownloadableContent();
        downloadableContent2.setName("test-name-2");
        downloadableContent2.setDescription("test-description-2");
        downloadableContent2.setSlug("test-slug-2");
        downloadableContent2.setReleaseDate(LocalDate.now());

        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setSlug("test-slug");
        game.addDownloadableContent(downloadableContent1);
        game.addDownloadableContent(downloadableContent2);
        game = testEntityManager.persistFlushFind(game);

        game.removeDownloadableContent(testEntityManager.find(DownloadableContent.class, downloadableContent1.getId()));

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getDownloadableContents().size()).isEqualTo(1);
        Assertions.assertThat(result.getDownloadableContents().iterator().next().getId())
                .isEqualTo(downloadableContent2.getId());
    }

    @Test
    void persist_withValidFranchiseRelationship_mapsRelationship() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setTitle("test-franchise");
        franchise.setDescription("test-franchise-description");
        franchise.setSlug("test-slug");
        franchise = testEntityManager.persistFlushFind(franchise);

        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setSlug("test-slug");
        game.setFranchiseId(franchise.getId());

        // Act
        Game result = testEntityManager.persistFlushFind(game);

        // Assert
        Assertions.assertThat(result.getFranchise()).isEqualTo(franchise);
        Assertions.assertThat(result.getFranchiseId()).isEqualTo(franchise.getId());
    }
}
