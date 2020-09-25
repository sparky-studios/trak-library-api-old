package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDate;

@DataJpaTest
class PlatformReleaseDateTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullRegion_throwsPersistenceException() {
        // Arrange
        PlatformReleaseDate platformReleaseDate = new PlatformReleaseDate();
        platformReleaseDate.setRegion(null);
        platformReleaseDate.setReleaseDate(LocalDate.now());

        Platform platform = new Platform();
        platform.setName("test-name");
        platform.setDescription("test-description");
        platform.addReleaseDate(platformReleaseDate);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(platform));
    }

    @Test
    void persist_withValidGameReleaseDateAndRelationship_mapsGameReleaseDate() {
        // Arrange
        PlatformReleaseDate platformReleaseDate = new PlatformReleaseDate();
        platformReleaseDate.setRegion(GameRegion.PAL);
        platformReleaseDate.setReleaseDate(LocalDate.now());

        Platform platform = new Platform();
        platform.setName("test-name");
        platform.setDescription("test-description");
        platform.addReleaseDate(platformReleaseDate);

        // Act
        PlatformReleaseDate result = testEntityManager.persistFlushFind(platform).getReleaseDates().iterator().next();

        // Assert
        Assertions.assertThat(result.getId()).isGreaterThan(0L);
        Assertions.assertThat(result.getPlatform()).isEqualTo(platform);
        Assertions.assertThat(result.getRegion()).isEqualTo(platformReleaseDate.getRegion());
        Assertions.assertThat(result.getReleaseDate()).isEqualTo(platformReleaseDate.getReleaseDate());
        Assertions.assertThat(result.getVersion()).isNotNull().isGreaterThanOrEqualTo(0L);
    }
}
