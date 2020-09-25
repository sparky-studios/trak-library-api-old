package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameRegion;
import com.sparkystudios.traklibrary.game.domain.GameReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class GameReleaseDateMapperTest {

    @Test
    void gameReleaseDateToGameReleaseDateDto_withNull_returnsNull() {
        // Act
        GameReleaseDateDto result = GameMappers.GAME_RELEASE_DATE_MAPPER.gameReleaseDateToGameReleaseDateDto(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    void gameReleaseDateToGameReleaseDateDto_withGameReleaseDate_mapsFields() {
        // Arrange
        GameReleaseDate gameReleaseDate = new GameReleaseDate();
        gameReleaseDate.setRegion(GameRegion.JAPAN);
        gameReleaseDate.setReleaseDate(LocalDate.now());
        gameReleaseDate.setVersion(2L);

        // Act
        GameReleaseDateDto result = GameMappers.GAME_RELEASE_DATE_MAPPER.gameReleaseDateToGameReleaseDateDto(gameReleaseDate);

        // Assert
        Assertions.assertEquals(gameReleaseDate.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(gameReleaseDate.getRegion(), result.getRegion(), "The mapped region does not match the entity.");
        Assertions.assertEquals(gameReleaseDate.getReleaseDate(), result.getReleaseDate(), "The mapped release date does not match the entity.");
        Assertions.assertEquals(gameReleaseDate.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
    }

    @Test
    void gameReleaseDateDtoToGameReleaseDate_withNull_returnsNull() {
        // Act
        GameReleaseDate result = GameMappers.GAME_RELEASE_DATE_MAPPER.gameReleaseDateDtoToGameReleaseDate(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    void gameReleaseDateDtoToGameReleaseDate_withGameReleaseDateDto_mapsFields() {
        // Arrange
        GameReleaseDateDto gameReleaseDateDto = new GameReleaseDateDto();
        gameReleaseDateDto.setRegion(GameRegion.JAPAN);
        gameReleaseDateDto.setReleaseDate(LocalDate.now());
        gameReleaseDateDto.setVersion(2L);

        // Act
        GameReleaseDate result = GameMappers.GAME_RELEASE_DATE_MAPPER.gameReleaseDateDtoToGameReleaseDate(gameReleaseDateDto);

        // Assert
        Assertions.assertEquals(gameReleaseDateDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(gameReleaseDateDto.getRegion(), result.getRegion(), "The mapped region does not match the DTO.");
        Assertions.assertEquals(gameReleaseDateDto.getReleaseDate(), result.getReleaseDate(), "The mapped release date does not match the DTO.");
        Assertions.assertEquals(gameReleaseDateDto.getVersion(), result.getVersion(), "The mapped version does not match the DTO.");
    }
}
