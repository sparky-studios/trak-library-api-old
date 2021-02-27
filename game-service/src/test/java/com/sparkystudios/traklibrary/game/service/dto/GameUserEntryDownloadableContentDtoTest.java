package com.sparkystudios.traklibrary.game.service.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class GameUserEntryDownloadableContentDtoTest {

    @Test
    void compareTo_withNullDownloadableContentName_returnsCorrectComparison() {
        // Arrange
        GameUserEntryDownloadableContentDto gameUserEntryDownloadableContentDto = new GameUserEntryDownloadableContentDto();
        gameUserEntryDownloadableContentDto.setId(5L);
        gameUserEntryDownloadableContentDto.setDownloadableContentName("");

        GameUserEntryDownloadableContentDto comparison = new GameUserEntryDownloadableContentDto();
        comparison.setId(4L);
        comparison.setDownloadableContentName("A");

        // Act
        int result = gameUserEntryDownloadableContentDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingDownloadableContentName_returnsCorrectComparison() {
        // Arrange
        GameUserEntryDownloadableContentDto gameUserEntryDownloadableContentDto = new GameUserEntryDownloadableContentDto();
        gameUserEntryDownloadableContentDto.setId(5L);
        gameUserEntryDownloadableContentDto.setDownloadableContentName("A");

        GameUserEntryDownloadableContentDto comparison = new GameUserEntryDownloadableContentDto();
        comparison.setId(4L);
        comparison.setDownloadableContentName("B");

        // Act
        int result = gameUserEntryDownloadableContentDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingId_returnsCorrectComparison() {
        // Arrange
        GameUserEntryDownloadableContentDto gameUserEntryDownloadableContentDto = new GameUserEntryDownloadableContentDto();
        gameUserEntryDownloadableContentDto.setId(4L);
        gameUserEntryDownloadableContentDto.setDownloadableContentName("A");

        GameUserEntryDownloadableContentDto comparison = new GameUserEntryDownloadableContentDto();
        comparison.setId(5L);
        comparison.setDownloadableContentName("A");

        // Act
        int result = gameUserEntryDownloadableContentDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withMatchingGameUserEntryDownloadableContentDtos_returnsCorrectComparison() {
        // Arrange
        GameUserEntryDownloadableContentDto gameUserEntryDownloadableContentDto = new GameUserEntryDownloadableContentDto();
        gameUserEntryDownloadableContentDto.setId(5L);
        gameUserEntryDownloadableContentDto.setDownloadableContentName("A");

        GameUserEntryDownloadableContentDto comparison = new GameUserEntryDownloadableContentDto();
        comparison.setId(5L);
        comparison.setDownloadableContentName("A");

        // Act
        int result = gameUserEntryDownloadableContentDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isZero();
    }
}
