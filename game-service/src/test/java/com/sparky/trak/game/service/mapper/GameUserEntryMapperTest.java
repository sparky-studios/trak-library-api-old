package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.Console;
import com.sparky.trak.game.domain.Game;
import com.sparky.trak.game.domain.GameUserEntry;
import com.sparky.trak.game.domain.GameUserEntryStatus;
import com.sparky.trak.game.service.dto.GameUserEntryDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameUserEntryMapperTest {

    @Test
    public void gameUserEntryToGameUserEntryDto_withGameUserEntry_mapsFields() {
        // Arrange
        Game game = new Game();
        game.setTitle("test-title");

        Console console = new Console();
        console.setName("test-name");

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setId(5L);
        gameUserEntry.setGameId(6L);
        gameUserEntry.setGame(game);
        gameUserEntry.setConsoleId(7L);
        gameUserEntry.setConsole(console);
        gameUserEntry.setUserId(8L);
        gameUserEntry.setStatus(GameUserEntryStatus.DROPPED);
        gameUserEntry.setRating((short)5);

        // Act
        GameUserEntryDto result = GameUserEntryMapper.INSTANCE.gameUserEntryToGameUserEntryDto(gameUserEntry);

        // Assert
        Assertions.assertEquals(gameUserEntry.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getGameId(), result.getGameId(), "The mapped game ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getGame().getTitle(), result.getGameTitle(), "The mapped game title does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getConsoleId(), result.getConsoleId(), "The mapped console ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getConsole().getName(), result.getConsoleName(), "The mapped console name does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getUserId(), result.getUserId(), "The mapped user ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getStatus(), result.getStatus(), "The mapped status does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getRating(), result.getRating(), "The mapped rating does not match the entity.");
    }

    @Test
    public void gameUserEntryDtoToGameUserEntry_withGameUserEntryDto_mapsFields() {
        // Arrange
        GameUserEntryDto gameUserEntryDto = new GameUserEntryDto();
        gameUserEntryDto.setId(5L);
        gameUserEntryDto.setGameId(6L);
        gameUserEntryDto.setConsoleId(7L);
        gameUserEntryDto.setUserId(8L);
        gameUserEntryDto.setStatus(GameUserEntryStatus.DROPPED);
        gameUserEntryDto.setRating((short)5);

        // Act
        GameUserEntry result = GameUserEntryMapper.INSTANCE.gameUserEntryDtoToGameUserEntry(gameUserEntryDto);

        // Assert
        Assertions.assertEquals(gameUserEntryDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(gameUserEntryDto.getGameId(), result.getGameId(), "The mapped game ID does not match the DTO.");
        Assertions.assertEquals(gameUserEntryDto.getConsoleId(), result.getConsoleId(), "The mapped console ID does not match the DTO.");
        Assertions.assertEquals(gameUserEntryDto.getUserId(), result.getUserId(), "The mapped user ID does not match the DTO.");
        Assertions.assertEquals(gameUserEntryDto.getStatus(), result.getStatus(), "The mapped status does not match the DTO.");
        Assertions.assertEquals(gameUserEntryDto.getRating(), result.getRating(), "The mapped rating does not match the DTO.");
    }
}
