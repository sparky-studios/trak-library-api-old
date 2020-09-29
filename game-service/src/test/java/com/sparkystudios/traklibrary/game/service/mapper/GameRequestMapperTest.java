package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameRequest;
import com.sparkystudios.traklibrary.game.service.dto.GameRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class GameRequestMapperTest {

    @Test
    void gameRequestToGameRequestDto_withNull_returnsNull() {
        // Act
        GameRequestDto result = GameMappers.GAME_REQUEST_MAPPER.gameRequestToGameRequestDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void gameRequestToGameRequestDto_withGameRequest_mapsFields() {
        // Arrange
        GameRequest gameRequest = new GameRequest();
        gameRequest.setId(5L);
        gameRequest.setTitle("test-game-request");
        gameRequest.setCompleted(true);
        gameRequest.setCompletedDate(LocalDateTime.now());
        gameRequest.setCreatedAt(LocalDateTime.now());
        gameRequest.setUpdatedAt(LocalDateTime.now());
        gameRequest.setUserId(3L);
        gameRequest.setVersion(2L);

        // Act
        GameRequestDto result = GameMappers.GAME_REQUEST_MAPPER.gameRequestToGameRequestDto(gameRequest);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameRequest.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(gameRequest.getTitle());
        Assertions.assertThat(result.isCompleted()).isEqualTo(gameRequest.isCompleted());
        Assertions.assertThat(result.getCompletedDate()).isEqualTo(gameRequest.getCompletedDate());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(gameRequest.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(gameRequest.getUpdatedAt());
        Assertions.assertThat(result.getUserId()).isEqualTo(gameRequest.getUserId());
        Assertions.assertThat(result.getVersion()).isEqualTo(gameRequest.getVersion());
    }

    @Test
    void gameRequestDtoToGameRequest_withNull_returnsNull() {
        // Act
        GameRequest result = GameMappers.GAME_REQUEST_MAPPER.gameRequestDtoToGameRequest(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void gameRequestDtoToGameRequest_withGameRequestDto_mapsFields() {
        // Arrange
        GameRequestDto gameRequestDto = new GameRequestDto();
        gameRequestDto.setId(5L);
        gameRequestDto.setCompleted(true);
        gameRequestDto.setCompletedDate(LocalDateTime.now());
        gameRequestDto.setCreatedAt(LocalDateTime.now());
        gameRequestDto.setUpdatedAt(LocalDateTime.now());
        gameRequestDto.setUserId(3L);
        gameRequestDto.setVersion(2L);

        // Act
        GameRequest result = GameMappers.GAME_REQUEST_MAPPER.gameRequestDtoToGameRequest(gameRequestDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameRequestDto.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(gameRequestDto.getTitle());
        Assertions.assertThat(result.isCompleted()).isEqualTo(gameRequestDto.isCompleted());
        Assertions.assertThat(result.getCompletedDate()).isEqualTo(gameRequestDto.getCompletedDate());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getUserId()).isEqualTo(gameRequestDto.getUserId());
        Assertions.assertThat(result.getVersion()).isEqualTo(gameRequestDto.getVersion());
    }
}
