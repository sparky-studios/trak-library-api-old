package com.traklibrary.game.service.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class PlatformDtoTest {

    @Test
    void compareTo_withNullName_returnsCorrectComparison() {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(5L);
        platformDto.setName("A");
        platformDto.setReleaseDate(LocalDate.now().plusDays(1));

        PlatformDto comparison = new PlatformDto();
        comparison.setId(4L);
        comparison.setName(null);
        comparison.setReleaseDate(LocalDate.now());

        // Act
        int result = platformDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingName_returnsCorrectComparison() {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(5L);
        platformDto.setName("A");
        platformDto.setReleaseDate(LocalDate.now().plusDays(1));

        PlatformDto comparison = new PlatformDto();
        comparison.setId(4L);
        comparison.setName("B");
        comparison.setReleaseDate(LocalDate.now());

        // Act
        int result = platformDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withNullReleaseDate_returnsCorrectComparison() {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(5L);
        platformDto.setName("A");
        platformDto.setReleaseDate(LocalDate.now().plusDays(1));

        PlatformDto comparison = new PlatformDto();
        comparison.setId(4L);
        comparison.setName("A");
        comparison.setReleaseDate(null);

        // Act
        int result = platformDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingReleaseDate_returnsCorrectComparison() {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(5L);
        platformDto.setName("A");
        platformDto.setReleaseDate(LocalDate.now());

        PlatformDto comparison = new PlatformDto();
        comparison.setId(4L);
        comparison.setName("A");
        comparison.setReleaseDate(LocalDate.now().plusDays(1));

        // Act
        int result = platformDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingId_returnsCorrectComparison() {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(4L);
        platformDto.setName("A");
        platformDto.setReleaseDate(LocalDate.now());

        PlatformDto comparison = new PlatformDto();
        comparison.setId(5L);
        comparison.setName("A");
        comparison.setReleaseDate(LocalDate.now());

        // Act
        int result = platformDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withMatchingPlatforms_returnsCorrectComparison() {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(5L);
        platformDto.setName("A");
        platformDto.setReleaseDate(LocalDate.now());

        PlatformDto comparison = new PlatformDto();
        comparison.setId(5L);
        comparison.setName("A");
        comparison.setReleaseDate(LocalDate.now());

        // Act
        int result = platformDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isZero();
    }
}
