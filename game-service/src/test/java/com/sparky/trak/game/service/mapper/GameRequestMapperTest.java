package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.GameRequest;
import com.sparky.trak.game.service.dto.GameRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class GameRequestMapperTest {

    @Test
    public void gameRequestToGameRequestDto_withGameRequest_mapsFields() {
        // Arrange
        GameRequest gameRequest = new GameRequest();
        gameRequest.setId(5L);
        gameRequest.setTitle("test-game-request");
        gameRequest.setCompleted(true);
        gameRequest.setCompletedDate(LocalDate.now());
        gameRequest.setUserId(3L);
        gameRequest.setVersion(2L);

        // Act
        GameRequestDto result = GameRequestMapper.INSTANCE.gameRequestToGameRequestDto(gameRequest);

        // Assert
        Assertions.assertEquals(gameRequest.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(gameRequest.getTitle(), result.getTitle(), "The mapped title does not match the entity.");
        Assertions.assertEquals(gameRequest.isCompleted(), result.isCompleted(), "The mapped completed does not match the entity.");
        Assertions.assertEquals(gameRequest.getCompletedDate(), result.getCompletedDate(), "The mapped completed date does not match the entity.");
        Assertions.assertEquals(gameRequest.getUserId(), result.getUserId(), "The mapped user ID does not match the entity.");
        Assertions.assertEquals(gameRequest.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
    }

    @Test
    public void gameRequestDtoToGameRequest_withGameRequestDto_mapsFields() {
        // Arrange
        GameRequestDto gameRequestDto = new GameRequestDto();
        gameRequestDto.setId(5L);
        gameRequestDto.setCompleted(true);
        gameRequestDto.setCompletedDate(LocalDate.now());
        gameRequestDto.setUserId(3L);
        gameRequestDto.setVersion(2L);

        // Act
        GameRequest result = GameRequestMapper.INSTANCE.gameRequestDtoToGameRequest(gameRequestDto);

        // Assert
        Assertions.assertEquals(gameRequestDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(gameRequestDto.getTitle(), result.getTitle(), "The mapped title does not match the DTO.");
        Assertions.assertEquals(gameRequestDto.isCompleted(), result.isCompleted(), "The mapped completed does not match the DTO.");
        Assertions.assertEquals(gameRequestDto.getCompletedDate(), result.getCompletedDate(), "The mapped completed date does not match the DTO.");
        Assertions.assertEquals(gameRequestDto.getUserId(), result.getUserId(), "The mapped user ID does not match the DTO.");
        Assertions.assertEquals(gameRequestDto.getVersion(), result.getVersion(), "The mapped version does not match the DTO.");
    }
}
