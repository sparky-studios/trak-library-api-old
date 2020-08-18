package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.AgeRating;
import com.traklibrary.game.domain.Game;
import com.traklibrary.game.service.dto.GameDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class GameMapperTest {

    @Test
    void gameToGameDto_withNull_returnsNull() {
        // Act
        GameDto result = GameMappers.GAME_MAPPER.gameToGameDto(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    void gameToGameDto_withGame_mapsFields() {
        // Arrange
        Game game = new Game();
        game.setId(5L);
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.ADULTS_ONLY);
        game.setVersion(1L);

        // Act
        GameDto result = GameMappers.GAME_MAPPER.gameToGameDto(game);

        // Assert
        Assertions.assertEquals(game.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(game.getTitle(), result.getTitle(), "The mapped title does not match the entity.");
        Assertions.assertEquals(game.getDescription(), result.getDescription(), "The mapped description does not match the entity.");
        Assertions.assertEquals(game.getReleaseDate(), result.getReleaseDate(), "The mapped release date does not match the entity.");
        Assertions.assertEquals(game.getAgeRating(), result.getAgeRating(), "The mapped age rating does not match the entity.");
        Assertions.assertEquals(game.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
    }

    @Test
    void gameDtoToGame_withNull_returnsNull() {
        // Act
        Game result = GameMappers.GAME_MAPPER.gameDtoToGame(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    void gameDtoToGame_withGameDto_mapsFields() {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setReleaseDate(LocalDate.now());
        gameDto.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        gameDto.setVersion(1L);

        // Act
        Game result = GameMappers.GAME_MAPPER.gameDtoToGame(gameDto);

        // Assert
        Assertions.assertEquals(gameDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(gameDto.getTitle(), result.getTitle(), "The mapped title does not match the DTO.");
        Assertions.assertEquals(gameDto.getDescription(), result.getDescription(), "The mapped description does not match the DTO.");
        Assertions.assertEquals(gameDto.getReleaseDate(), result.getReleaseDate(), "The mapped release date does not match the DTO.");
        Assertions.assertEquals(gameDto.getAgeRating(), result.getAgeRating(), "The mapped age rating does not match the DTO.");
        Assertions.assertEquals(gameDto.getVersion(), result.getVersion(), "The mapped version does not match the DTO.");
    }
}
