package com.sparkystudios.traklibrary.game.service.dto;

import com.sparkystudios.traklibrary.game.domain.GameRegion;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class PlatformReleaseDateDtoTest {

    @Test
    void compareTo_withNullRegion_returnsCorrectComparison() {
        // Arrange
        PlatformReleaseDateDto platformReleaseDateDto = new PlatformReleaseDateDto();
        platformReleaseDateDto.setRegion(GameRegion.PAL);
        platformReleaseDateDto.setReleaseDate(LocalDate.now());
        PlatformReleaseDateDto comparison = new PlatformReleaseDateDto();
        comparison.setRegion(null);
        comparison.setReleaseDate(LocalDate.now());

        // Act
        int result = platformReleaseDateDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingRegion_returnsCorrectComparison() {
        // Arrange
        PlatformReleaseDateDto platformReleaseDateDto = new PlatformReleaseDateDto();
        platformReleaseDateDto.setRegion(GameRegion.PAL);
        platformReleaseDateDto.setReleaseDate(LocalDate.now());
        PlatformReleaseDateDto comparison = new PlatformReleaseDateDto();
        comparison.setRegion(GameRegion.JAPAN);
        comparison.setReleaseDate(LocalDate.now());

        // Act
        int result = platformReleaseDateDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withMatchingRegion_returnsCorrectComparison() {
        // Arrange
        PlatformReleaseDateDto platformReleaseDateDto = new PlatformReleaseDateDto();
        platformReleaseDateDto.setRegion(GameRegion.PAL);
        platformReleaseDateDto.setReleaseDate(LocalDate.now());
        PlatformReleaseDateDto comparison = new PlatformReleaseDateDto();
        comparison.setRegion(GameRegion.PAL);
        comparison.setReleaseDate(LocalDate.now());

        // Act
        int result = platformReleaseDateDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isZero();
    }
}
