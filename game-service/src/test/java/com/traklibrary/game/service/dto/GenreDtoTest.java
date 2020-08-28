package com.traklibrary.game.service.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class GenreDtoTest {

    @Test
    void compareTo_withNullName_returnsCorrectComparison() {
        // Arrange
        GenreDto genreDto = new GenreDto();
        genreDto.setId(5L);
        genreDto.setName("A");

        GenreDto comparison = new GenreDto();
        comparison.setId(4L);
        comparison.setName(null);

        // Act
        int result = genreDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withAscendingName_returnsCorrectComparison() {
        // Arrange
        GenreDto genreDto = new GenreDto();
        genreDto.setId(5L);
        genreDto.setName("A");

        GenreDto comparison = new GenreDto();
        comparison.setId(4L);
        comparison.setName("B");

        // Act
        int result = genreDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }
    
    @Test
    void compareTo_withAscendingId_returnsCorrectComparison() {
        // Arrange
        GenreDto genreDto = new GenreDto();
        genreDto.setId(4L);
        genreDto.setName("A");

        GenreDto comparison = new GenreDto();
        comparison.setId(5L);
        comparison.setName("A");

        // Act
        int result = genreDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isEqualTo(-1);
    }

    @Test
    void compareTo_withMatchingPlatforms_returnsCorrectComparison() {
        // Arrange
        GenreDto genreDto = new GenreDto();
        genreDto.setId(5L);
        genreDto.setName("A");

        GenreDto comparison = new GenreDto();
        comparison.setId(5L);
        comparison.setName("A");

        // Act
        int result = genreDto.compareTo(comparison);

        // Assert
        Assertions.assertThat(result).isZero();
    }
}
