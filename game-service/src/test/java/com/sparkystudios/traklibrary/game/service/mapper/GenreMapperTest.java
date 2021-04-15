package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Genre;
import com.sparkystudios.traklibrary.game.service.dto.GenreDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        GenreMapperImpl.class,
})
class GenreMapperTest {

    @Autowired
    private GenreMapper genreMapper;

    @Test
    void fromGenre_withNull_returnsNull() {
        // Act
        GenreDto result = genreMapper.fromGenre(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void fromGenre_withGenre_mapsFields() {
        // Arrange
        Genre genre = new Genre();
        genre.setId(5L);
        genre.setName("test-name");
        genre.setDescription("test-description");
        genre.setCreatedAt(LocalDateTime.now());
        genre.setUpdatedAt(LocalDateTime.now());
        genre.setVersion(1L);

        // Act
        GenreDto result = genreMapper.fromGenre(genre);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(genre.getId());
        Assertions.assertThat(result.getName()).isEqualTo(genre.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(genre.getDescription());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(genre.getVersion());
    }

    @Test
    void toGenre_withNull_returnsNull() {
        // Act
        Genre result = genreMapper.toGenre(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void toGenre_withGenreDto_mapsFields() {
        // Arrange
        GenreDto genreDto = new GenreDto();
        genreDto.setId(5L);
        genreDto.setName("Test Name");
        genreDto.setDescription("test-description");
        genreDto.setCreatedAt(LocalDateTime.now());
        genreDto.setUpdatedAt(LocalDateTime.now());
        genreDto.setVersion(1L);

        // Act
        Genre result = genreMapper.toGenre(genreDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(genreDto.getId());
        Assertions.assertThat(result.getName()).isEqualTo(genreDto.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(genreDto.getDescription());
        Assertions.assertThat(result.getSlug()).isEqualTo("test-name");
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(genreDto.getVersion());
    }
}
