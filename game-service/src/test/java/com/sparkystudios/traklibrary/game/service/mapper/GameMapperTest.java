package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

class GameMapperTest {

    @Test
    void gameToGameDto_withNull_returnsNull() {
        // Act
        GameDto result = GameMappers.GAME_MAPPER.gameToGameDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void gameToGameDto_withGame_mapsFields() {
        // Arrange
        GameReleaseDate gameReleaseDate = new GameReleaseDate();
        gameReleaseDate.setRegion(GameRegion.PAL);
        gameReleaseDate.setReleaseDate(LocalDate.now());
        gameReleaseDate.setVersion(1L);

        Game game = new Game();
        game.setId(5L);
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.setAgeRating(AgeRating.ADULTS_ONLY);
        game.getGameModes().add(GameMode.MULTI_PLAYER);
        game.setFranchiseId(5L);
        game.setCreatedAt(LocalDateTime.now());
        game.setUpdatedAt(LocalDateTime.now());
        game.setVersion(1L);
        game.addReleaseDate(gameReleaseDate);

        // Act
        GameDto result = GameMappers.GAME_MAPPER.gameToGameDto(game);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(game.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(game.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(game.getDescription());
        Assertions.assertThat(result.getAgeRating()).isEqualTo(game.getAgeRating());
        Assertions.assertThat(result.getGameModes()).isEqualTo(game.getGameModes());
        Assertions.assertThat(result.getFranchiseId()).isEqualTo(game.getFranchiseId());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(game.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(game.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(game.getVersion());
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
    }

    @Test
    void gameDtoToGame_withNull_returnsNull() {
        // Act`
        Game result = GameMappers.GAME_MAPPER.gameDtoToGame(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void gameDtoToGame_withGameDto_mapsFields() {
        // Arrange
        GameReleaseDateDto gameReleaseDateDto = new GameReleaseDateDto();
        gameReleaseDateDto.setRegion(GameRegion.PAL);
        gameReleaseDateDto.setReleaseDate(LocalDate.now());
        gameReleaseDateDto.setVersion(1L);

        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        gameDto.getGameModes().add(GameMode.MULTI_PLAYER);
        gameDto.setFranchiseId(5L);
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);
        gameDto.getReleaseDates().add(gameReleaseDateDto);

        // Act
        Game result = GameMappers.GAME_MAPPER.gameDtoToGame(gameDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameDto.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(gameDto.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(gameDto.getDescription());
        Assertions.assertThat(result.getAgeRating()).isEqualTo(gameDto.getAgeRating());
        Assertions.assertThat(result.getGameModes()).isEqualTo(gameDto.getGameModes());
        Assertions.assertThat(result.getFranchiseId()).isEqualTo(gameDto.getFranchiseId());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(gameDto.getVersion());
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
    }
}
