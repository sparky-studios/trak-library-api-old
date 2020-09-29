package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Genre;
import com.sparkystudios.traklibrary.game.service.dto.GenreDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class GenreMapperTest {

    @Test
    void genreToGenreDto_withNull_returnsNull() {
        // Act
        GenreDto result = GameMappers.GENRE_MAPPER.genreToGenreDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void genreToGenreDto_withGenre_mapsFields() {
        // Arrange
        Genre genre = new Genre();
        genre.setId(5L);
        genre.setName("test-name");
        genre.setDescription("test-description");
        genre.setCreatedAt(LocalDateTime.now());
        genre.setUpdatedAt(LocalDateTime.now());
        genre.setVersion(1L);

        // Act
        GenreDto result = GameMappers.GENRE_MAPPER.genreToGenreDto(genre);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(genre.getId());
        Assertions.assertThat(result.getName()).isEqualTo(genre.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(genre.getDescription());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(genre.getVersion());
    }

    @Test
    void genreDtoToGenre_withNull_returnsNull() {
        // Act
        Genre result = GameMappers.GENRE_MAPPER.genreDtoToGenre(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void genreDtoToGenre_withGenreDto_mapsFields() {
        // Arrange
        GenreDto genreDto = new GenreDto();
        genreDto.setId(5L);
        genreDto.setName("test-name");
        genreDto.setDescription("test-description");
        genreDto.setCreatedAt(LocalDateTime.now());
        genreDto.setUpdatedAt(LocalDateTime.now());
        genreDto.setVersion(1L);

        // Act
        Genre result = GameMappers.GENRE_MAPPER.genreDtoToGenre(genreDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(genreDto.getId());
        Assertions.assertThat(result.getName()).isEqualTo(genreDto.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(genreDto.getDescription());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(genreDto.getVersion());
    }
}
