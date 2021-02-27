package com.sparkystudios.traklibrary.game.service.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DownloadableContentDtoTest {

    @Test
    void compareTo_withNullName_returnsCorrectComparison() {
        // Arrange
        DownloadableContentDto downloadableContentDto = new DownloadableContentDto();
        downloadableContentDto.setId(5L);
        downloadableContentDto.setName("A");

        DownloadableContentDto comparison = new DownloadableContentDto();
        comparison.setId(4L);
        comparison.setName(null);

        // Act
        int result = downloadableContentDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void compareTo_withAscendingName_returnsCorrectComparison() {
        // Arrange
        DownloadableContentDto downloadableContentDto = new DownloadableContentDto();
        downloadableContentDto.setId(5L);
        downloadableContentDto.setName("A");

        DownloadableContentDto comparison = new DownloadableContentDto();
        comparison.setId(4L);
        comparison.setName("B");

        // Act
        int result = downloadableContentDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingId_returnsCorrectComparison() {
        // Arrange
        DownloadableContentDto downloadableContentDto = new DownloadableContentDto();
        downloadableContentDto.setId(4L);
        downloadableContentDto.setName("A");

        DownloadableContentDto comparison = new DownloadableContentDto();
        comparison.setId(5L);
        comparison.setName("A");

        // Act
        int result = downloadableContentDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withMatchingPlatforms_returnsCorrectComparison() {
        // Arrange
        DownloadableContentDto downloadableContentDto = new DownloadableContentDto();
        downloadableContentDto.setId(5L);
        downloadableContentDto.setName("A");

        DownloadableContentDto comparison = new DownloadableContentDto();
        comparison.setId(5L);
        comparison.setName("A");

        // Act
        int result = downloadableContentDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isZero();
    }
}
