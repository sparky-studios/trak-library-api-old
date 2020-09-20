package com.sparkystudios.traklibrary.game.service.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class PublisherDtoTest {

    @Test
    void compareTo_withNullName_returnsCorrectComparison() {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(5L);
        publisherDto.setName("A");
        publisherDto.setFoundedDate(LocalDate.now().plusDays(1));

        PublisherDto comparison = new PublisherDto();
        comparison.setId(4L);
        comparison.setName(null);
        comparison.setFoundedDate(LocalDate.now());

        // Act
        int result = publisherDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingName_returnsCorrectComparison() {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(5L);
        publisherDto.setName("A");
        publisherDto.setFoundedDate(LocalDate.now().plusDays(1));

        PublisherDto comparison = new PublisherDto();
        comparison.setId(4L);
        comparison.setName("B");
        comparison.setFoundedDate(LocalDate.now());

        // Act
        int result = publisherDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withNullFoundedDate_returnsCorrectComparison() {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(5L);
        publisherDto.setName("A");
        publisherDto.setFoundedDate(LocalDate.now().plusDays(1));

        PublisherDto comparison = new PublisherDto();
        comparison.setId(4L);
        comparison.setName("A");
        comparison.setFoundedDate(null);

        // Act
        int result = publisherDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingFoundedDate_returnsCorrectComparison() {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(5L);
        publisherDto.setName("A");
        publisherDto.setFoundedDate(LocalDate.now());

        PublisherDto comparison = new PublisherDto();
        comparison.setId(4L);
        comparison.setName("A");
        comparison.setFoundedDate(LocalDate.now().plusDays(1));

        // Act
        int result = publisherDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingId_returnsCorrectComparison() {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(4L);
        publisherDto.setName("A");
        publisherDto.setFoundedDate(LocalDate.now());

        PublisherDto comparison = new PublisherDto();
        comparison.setId(5L);
        comparison.setName("A");
        comparison.setFoundedDate(LocalDate.now());

        // Act
        int result = publisherDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withMatchingPlatforms_returnsCorrectComparison() {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(5L);
        publisherDto.setName("A");
        publisherDto.setFoundedDate(LocalDate.now());

        PublisherDto comparison = new PublisherDto();
        comparison.setId(5L);
        comparison.setName("A");
        comparison.setFoundedDate(LocalDate.now());

        // Act
        int result = publisherDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isZero();
    }
}
