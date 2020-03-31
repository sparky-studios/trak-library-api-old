package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.Genre;
import com.sparky.trak.game.service.dto.GenreDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GenreMapperTest {

    @Test
    public void genreToGenreDto_withGenre_mapsFields() {
        // Arrange
        Genre genre = new Genre();
        genre.setId(5L);
        genre.setName("test-name");
        genre.setDescription("test-description");
        genre.setVersion(1L);

        // Act
        GenreDto result = GenreMapper.INSTANCE.genreToGenreDto(genre);

        // Assert
        Assertions.assertEquals(genre.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(genre.getName(), result.getName(), "The mapped title does not match the entity.");
        Assertions.assertEquals(genre.getDescription(), result.getDescription(), "The mapped description does not match the entity.");
        Assertions.assertEquals(genre.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
    }

    @Test
    public void genreDtoToGenre_withGenreDto_mapsFields() {
        // Arrange
        GenreDto genreDto = new GenreDto();
        genreDto.setId(5L);
        genreDto.setName("test-name");
        genreDto.setDescription("test-description");
        genreDto.setVersion(1L);

        // Act
        Genre result = GenreMapper.INSTANCE.genreDtoToGenre(genreDto);

        // Assert
        Assertions.assertEquals(genreDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(genreDto.getName(), result.getName(), "The mapped title does not match the DTO.");
        Assertions.assertEquals(genreDto.getDescription(), result.getDescription(), "The mapped description does not match the DTO.");
        Assertions.assertEquals(genreDto.getVersion(), result.getVersion(), "The mapped version does not match the DTO.");
    }
}
