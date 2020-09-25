package com.sparkystudios.traklibrary.game.service.dto;

import com.sparkystudios.traklibrary.game.domain.GameRegion;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class GameReleaseDateDtoTest {

    @Test
    void compareTo_withNullRegion_returnsCorrectComparison() {
        // Arrange
        GameReleaseDateDto gameReleaseDateDto = new GameReleaseDateDto();
        gameReleaseDateDto.setRegion(GameRegion.PAL);
        gameReleaseDateDto.setReleaseDate(LocalDate.now());
        GameReleaseDateDto comparison = new GameReleaseDateDto();
        comparison.setRegion(null);
        comparison.setReleaseDate(LocalDate.now());

        // Act
        int result = gameReleaseDateDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingRegion_returnsCorrectComparison() {
        // Arrange
        GameReleaseDateDto gameReleaseDateDto = new GameReleaseDateDto();
        gameReleaseDateDto.setRegion(GameRegion.PAL);
        gameReleaseDateDto.setReleaseDate(LocalDate.now());
        GameReleaseDateDto comparison = new GameReleaseDateDto();
        comparison.setRegion(GameRegion.JAPAN);
        comparison.setReleaseDate(LocalDate.now());

        // Act
        int result = gameReleaseDateDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withMatchingRegion_returnsCorrectComparison() {
        // Arrange
        GameReleaseDateDto gameReleaseDateDto = new GameReleaseDateDto();
        gameReleaseDateDto.setRegion(GameRegion.PAL);
        gameReleaseDateDto.setReleaseDate(LocalDate.now());
        GameReleaseDateDto comparison = new GameReleaseDateDto();
        comparison.setRegion(GameRegion.PAL);
        comparison.setReleaseDate(LocalDate.now());

        // Act
        int result = gameReleaseDateDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isZero();
    }
}
