package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.*;
import com.sparky.trak.game.service.dto.GameUserEntryDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

public class GameUserEntryMapperTest {

    @Test
    public void gameUserEntryToGameUserEntryDto_withNull_returnsNull() {
        // Act
        GameUserEntryDto result = GameUserEntryMapper.INSTANCE.gameUserEntryToGameUserEntryDto(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    public void gameUserEntryToGameUserEntryDto_withGameUserEntry_mapsFields() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setName("publisher-name");

        GamePublisherXref gamePublisherXref = new GamePublisherXref();
        gamePublisherXref.setPublisher(publisher);

        Game game = new Game();
        game.setTitle("test-title");
        game.setReleaseDate(LocalDate.now());
        game.setGamePublisherXrefs(Collections.singleton(gamePublisherXref));

        Platform platform = new Platform();
        platform.setName("test-name");

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setId(5L);
        gameUserEntry.setGameId(6L);
        gameUserEntry.setGame(game);
        gameUserEntry.setPlatformId(7L);
        gameUserEntry.setPlatform(platform);
        gameUserEntry.setUserId(8L);
        gameUserEntry.setStatus(GameUserEntryStatus.DROPPED);
        gameUserEntry.setRating((short)5);

        // Act
        GameUserEntryDto result = GameUserEntryMapper.INSTANCE.gameUserEntryToGameUserEntryDto(gameUserEntry);

        // Assert
        Assertions.assertEquals(gameUserEntry.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getGameId(), result.getGameId(), "The mapped game ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getGame().getTitle(), result.getGameTitle(), "The mapped game title does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getGame().getReleaseDate(), result.getGameReleaseDate(), "The mapped game release date does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getPlatformId(), result.getPlatformId(), "The mapped platform ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getPlatform().getName(), result.getPlatformName(), "The mapped platform name does not match the entity.");
        Assertions.assertEquals(publisher.getName(), result.getPublishers().iterator().next(), "The mapped publisher does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getUserId(), result.getUserId(), "The mapped user ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getStatus(), result.getStatus(), "The mapped status does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getRating(), result.getRating(), "The mapped rating does not match the entity.");
    }

    @Test
    public void gameUserEntryDtpToGameUserEntry_withNull_returnsNull() {
        // Act
        GameUserEntry result = GameUserEntryMapper.INSTANCE.gameUserEntryDtoToGameUserEntry(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    public void gameUserEntryDtoToGameUserEntry_withGameUserEntryDto_mapsFields() {
        // Arrange
        GameUserEntryDto gameUserEntryDto = new GameUserEntryDto();
        gameUserEntryDto.setId(5L);
        gameUserEntryDto.setGameId(6L);
        gameUserEntryDto.setPlatformId(7L);
        gameUserEntryDto.setUserId(8L);
        gameUserEntryDto.setStatus(GameUserEntryStatus.DROPPED);
        gameUserEntryDto.setRating((short)5);

        // Act
        GameUserEntry result = GameUserEntryMapper.INSTANCE.gameUserEntryDtoToGameUserEntry(gameUserEntryDto);

        // Assert
        Assertions.assertEquals(gameUserEntryDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(gameUserEntryDto.getGameId(), result.getGameId(), "The mapped game ID does not match the DTO.");
        Assertions.assertEquals(gameUserEntryDto.getPlatformId(), result.getPlatformId(), "The mapped platform ID does not match the DTO.");
        Assertions.assertEquals(gameUserEntryDto.getUserId(), result.getUserId(), "The mapped user ID does not match the DTO.");
        Assertions.assertEquals(gameUserEntryDto.getStatus(), result.getStatus(), "The mapped status does not match the DTO.");
        Assertions.assertEquals(gameUserEntryDto.getRating(), result.getRating(), "The mapped rating does not match the DTO.");
    }
}
