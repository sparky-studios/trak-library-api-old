package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameRegion;
import com.sparkystudios.traklibrary.game.domain.GameReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

class GameReleaseDateMapperTest {

    @Test
    void gameReleaseDateToGameReleaseDateDto_withNull_returnsNull() {
        // Act
        GameReleaseDateDto result = GameMappers.GAME_RELEASE_DATE_MAPPER.gameReleaseDateToGameReleaseDateDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void gameReleaseDateToGameReleaseDateDto_withGameReleaseDate_mapsFields() {
        // Arrange
        GameReleaseDate gameReleaseDate = new GameReleaseDate();
        gameReleaseDate.setRegion(GameRegion.JAPAN);
        gameReleaseDate.setReleaseDate(LocalDate.now());
        gameReleaseDate.setCreatedAt(LocalDateTime.now());
        gameReleaseDate.setUpdatedAt(LocalDateTime.now());
        gameReleaseDate.setVersion(2L);

        // Act
        GameReleaseDateDto result = GameMappers.GAME_RELEASE_DATE_MAPPER.gameReleaseDateToGameReleaseDateDto(gameReleaseDate);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameReleaseDate.getId());
        Assertions.assertThat(result.getRegion()).isEqualTo(gameReleaseDate.getRegion());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(gameReleaseDate.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(gameReleaseDate.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(gameReleaseDate.getVersion());
    }

    @Test
    void gameReleaseDateDtoToGameReleaseDate_withNull_returnsNull() {
        // Act
        GameReleaseDate result = GameMappers.GAME_RELEASE_DATE_MAPPER.gameReleaseDateDtoToGameReleaseDate(null);

        // Assert
        Assertions.assertThat(result).isNull();
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
        Assertions.assertThat(result.getId()).isEqualTo(gameReleaseDateDto.getId());
        Assertions.assertThat(result.getRegion()).isEqualTo(gameReleaseDateDto.getRegion());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(gameReleaseDateDto.getVersion());
    }
}
