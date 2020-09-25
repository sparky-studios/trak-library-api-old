package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.AgeRating;
import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.GameRegion;
import com.sparkystudios.traklibrary.game.domain.GameReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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
        game.setVersion(1L);
        game.addReleaseDate(gameReleaseDate);

        // Act
        GameDto result = GameMappers.GAME_MAPPER.gameToGameDto(game);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(game.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(game.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(game.getDescription());
        Assertions.assertThat(result.getAgeRating()).isEqualTo(game.getAgeRating());
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
        gameDto.setVersion(1L);
        gameDto.getReleaseDates().add(gameReleaseDateDto);

        // Act
        Game result = GameMappers.GAME_MAPPER.gameDtoToGame(gameDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameDto.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(gameDto.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(gameDto.getDescription());
        Assertions.assertThat(result.getAgeRating()).isEqualTo(gameDto.getAgeRating());
        Assertions.assertThat(result.getVersion()).isEqualTo(gameDto.getVersion());
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
    }
}
