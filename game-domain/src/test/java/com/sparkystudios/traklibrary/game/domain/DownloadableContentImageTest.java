package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDate;

@DataJpaTest
class DownloadableContentImageTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullDownloadableContent_throwsPersistenceException() {
        // Arrange
        DownloadableContentImage downloadableContentImage = new DownloadableContentImage();
        downloadableContentImage.setFilename("test-file.png");
        downloadableContentImage.setImageSize(ImageSize.MEDIUM);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(downloadableContentImage));
    }

    @Test
    void persist_withValidDownloadableContent_mapsDownloadableContentImage() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setSlug("test-slug");
        game = testEntityManager.persistFlushFind(game);

        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName("dlc-title");
        downloadableContent.setDescription("dlc-description");
        downloadableContent.setSlug("test-slug");
        downloadableContent.setReleaseDate(LocalDate.now());
        downloadableContent.setGame(game);
        downloadableContent = testEntityManager.persistFlushFind(downloadableContent);

        DownloadableContentImage downloadableContentImage = new DownloadableContentImage();
        downloadableContentImage.setDownloadableContentId(downloadableContent.getId());
        downloadableContentImage.setFilename("test-file.png");
        downloadableContentImage.setImageSize(ImageSize.MEDIUM);

        // Act
        DownloadableContentImage result = testEntityManager.persistFlushFind(downloadableContentImage);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getDownloadableContentId()).isEqualTo(downloadableContent.getId());
        Assertions.assertThat(result.getDownloadableContent().getId())
                .isEqualTo(result.getDownloadableContent().getId());
        Assertions.assertThat(result.getFilename()).isEqualTo(downloadableContentImage.getFilename());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }

    @Test
    void persist_withMultipleImagesForSameGameWithSameSize_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setSlug("test-slug");
        game = testEntityManager.persistFlushFind(game);

        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName("dlc-title");
        downloadableContent.setDescription("dlc-description");
        downloadableContent.setSlug("test-slug");
        downloadableContent.setReleaseDate(LocalDate.now());
        downloadableContent.setGame(game);
        downloadableContent = testEntityManager.persistFlushFind(downloadableContent);

        DownloadableContentImage downloadableContentImage1 = new DownloadableContentImage();
        downloadableContentImage1.setDownloadableContentId(downloadableContent.getId());
        downloadableContentImage1.setFilename("test-file.png");
        downloadableContentImage1.setImageSize(ImageSize.MEDIUM);

        DownloadableContentImage downloadableContentImage2 = new DownloadableContentImage();
        downloadableContentImage2.setDownloadableContentId(downloadableContent.getId());
        downloadableContentImage2.setFilename("test-file2.png");
        downloadableContentImage2.setImageSize(ImageSize.MEDIUM);

        // Act
        testEntityManager.persistFlushFind(downloadableContentImage1);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(downloadableContentImage2));
    }
}
