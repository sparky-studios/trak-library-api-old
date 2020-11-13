package com.sparkystudios.traklibrary.game.service.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class GameUserEntryPlatformDtoTest {

    @Test
    void compareTo_withNullPlatformName_returnsCorrectComparison() {
        // Arrange
        GameUserEntryPlatformDto gameUserEntryGameUserEntryPlatformDto = new GameUserEntryPlatformDto();
        gameUserEntryGameUserEntryPlatformDto.setId(5L);
        gameUserEntryGameUserEntryPlatformDto.setPlatformName("A");

        GameUserEntryPlatformDto comparison = new GameUserEntryPlatformDto();
        comparison.setId(4L);
        comparison.setPlatformName(null);

        // Act
        int result = gameUserEntryGameUserEntryPlatformDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingPlatformName_returnsCorrectComparison() {
        // Arrange
        GameUserEntryPlatformDto gameUserEntryGameUserEntryPlatformDto = new GameUserEntryPlatformDto();
        gameUserEntryGameUserEntryPlatformDto.setId(5L);
        gameUserEntryGameUserEntryPlatformDto.setPlatformName("A");

        GameUserEntryPlatformDto comparison = new GameUserEntryPlatformDto();
        comparison.setId(4L);
        comparison.setPlatformName("B");

        // Act
        int result = gameUserEntryGameUserEntryPlatformDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingId_returnsCorrectComparison() {
        // Arrange
        GameUserEntryPlatformDto gameUserEntryGameUserEntryPlatformDto = new GameUserEntryPlatformDto();
        gameUserEntryGameUserEntryPlatformDto.setId(4L);
        gameUserEntryGameUserEntryPlatformDto.setPlatformName("A");

        GameUserEntryPlatformDto comparison = new GameUserEntryPlatformDto();
        comparison.setId(5L);
        comparison.setPlatformName("A");

        // Act
        int result = gameUserEntryGameUserEntryPlatformDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withMatchingGameUserEntryPlatforms_returnsCorrectComparison() {
        // Arrange
        GameUserEntryPlatformDto gameUserEntryGameUserEntryPlatformDto = new GameUserEntryPlatformDto();
        gameUserEntryGameUserEntryPlatformDto.setId(5L);
        gameUserEntryGameUserEntryPlatformDto.setPlatformName("A");

        GameUserEntryPlatformDto comparison = new GameUserEntryPlatformDto();
        comparison.setId(5L);
        comparison.setPlatformName("A");

        // Act
        int result = gameUserEntryGameUserEntryPlatformDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isZero();
    }
}
