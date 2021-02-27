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
class DownloadableContentTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullName_throwsPersistenceException() {
        // Arrange
        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName(null);
        downloadableContent.setDescription("test-description");
        downloadableContent.setReleaseDate(LocalDate.now());

        Game game = new Game();
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.setAgeRating(AgeRating.MATURE);
        game.addDownloadableContent(downloadableContent);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(game));
    }

    @Test
    void persist_withNameExceedingLength_throwsPersistenceException() {
        // Arrange
        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName(String.join("", Collections.nCopies(300, "t")));
        downloadableContent.setDescription("test-description");
        downloadableContent.setReleaseDate(LocalDate.now());

        Game game = new Game();
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.setAgeRating(AgeRating.MATURE);
        game.addDownloadableContent(downloadableContent);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(game));
    }

    @Test
    void persist_withNullDescription_throwsPersistenceException() {
        // Arrange
        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName("test-name");
        downloadableContent.setDescription(null);
        downloadableContent.setReleaseDate(LocalDate.now());

        Game game = new Game();
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.setAgeRating(AgeRating.MATURE);
        game.addDownloadableContent(downloadableContent);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(game));
    }


    @Test
    void persist_withDescriptionExceedingLength_throwsPersistenceException() {
        // Arrange
        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName("test-name");
        downloadableContent.setDescription(String.join("", Collections.nCopies(5000, "t")));
        downloadableContent.setReleaseDate(LocalDate.now());

        Game game = new Game();
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.setAgeRating(AgeRating.MATURE);
        game.addDownloadableContent(downloadableContent);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(game));
    }

    @Test
    void persist_withValidDownloadableContentAndRelationship_mapsDownloadableContent() {
        // Arrange
        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName("test-name");
        downloadableContent.setDescription("test-description");
        downloadableContent.setReleaseDate(LocalDate.now());

        Game game = new Game();
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.setAgeRating(AgeRating.MATURE);
        game.addDownloadableContent(downloadableContent);

        // Act
        game = testEntityManager.persistFlushFind(game);
        DownloadableContent result = game.getDownloadableContents().iterator().next();

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getName()).isEqualTo(downloadableContent.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(downloadableContent.getDescription());
        Assertions.assertThat(result.getReleaseDate()).isEqualTo(result.getReleaseDate());
        Assertions.assertThat(result.getGame().getId())
                .isEqualTo(game.getId());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }
}
