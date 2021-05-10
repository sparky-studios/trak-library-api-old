package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;

@DataJpaTest
class PlatformImageTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullPlatform_throwsPersistenceException() {
        // Arrange
        PlatformImage platformImage = new PlatformImage();
        platformImage.setFilename("filename.png");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(platformImage));
    }

    @Test
    void persist_withValidPlatform_mapsPlatformImage() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("test-company");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = testEntityManager.persistFlushFind(platform);

        PlatformImage platformImage = new PlatformImage();
        platformImage.setFilename("filename.png");
        platformImage.setPlatformId(platform.getId());

        // Act
        PlatformImage result = testEntityManager.persistFlushFind(platformImage);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getPlatformId()).isEqualTo(platform.getId());
        Assertions.assertThat(result.getPlatform().getId())
                .isEqualTo(result.getPlatform().getId());
        Assertions.assertThat(result.getFilename()).isEqualTo(platformImage.getFilename());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }

    @Test
    void persist_withMultipleImagesForSameCompany_throwsPersistenceException() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("test-company");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = testEntityManager.persistFlushFind(platform);

        PlatformImage platformImage1 = new PlatformImage();
        platformImage1.setFilename("filename1.png");
        platformImage1.setPlatformId(platform.getId());

        PlatformImage platformImage2 = new PlatformImage();
        platformImage2.setFilename("filename2.png");
        platformImage2.setPlatformId(platform.getId());

        // Act
        testEntityManager.persistFlushFind(platformImage1);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(platformImage2));
    }
}
