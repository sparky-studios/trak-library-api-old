package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class GameUserEntryMapperTest {

    @Test
    void gameUserEntryToGameUserEntryDto_withNull_returnsNull() {
        // Act
        GameUserEntryDto result = GameMappers.GAME_USER_ENTRY_MAPPER.gameUserEntryToGameUserEntryDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void gameUserEntryToGameUserEntryDto_withGameUserEntry_mapsFields() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setName("publisher-name");

        Game game = new Game();
        game.setTitle("test-title");
        game.addPublisher(publisher);

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
        gameUserEntry.setCreatedAt(LocalDateTime.now());
        gameUserEntry.setUpdatedAt(LocalDateTime.now());

        // Act
        GameUserEntryDto result = GameMappers.GAME_USER_ENTRY_MAPPER.gameUserEntryToGameUserEntryDto(gameUserEntry);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameUserEntry.getId());
        Assertions.assertThat(result.getGameId()).isEqualTo(gameUserEntry.getGameId());
        Assertions.assertThat(result.getGameTitle()).isEqualTo(game.getTitle());
        Assertions.assertThat(result.getPlatformId()).isEqualTo(gameUserEntry.getPlatformId());
        Assertions.assertThat(result.getPlatformName()).isEqualTo(platform.getName());
        Assertions.assertThat(result.getUserId()).isEqualTo(gameUserEntry.getUserId());
        Assertions.assertThat(result.getStatus()).isEqualTo(gameUserEntry.getStatus());
        Assertions.assertThat(result.getRating()).isEqualTo(gameUserEntry.getRating());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(gameUserEntry.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(gameUserEntry.getUpdatedAt());
    }

    @Test
    void gameUserEntryDtpToGameUserEntry_withNull_returnsNull() {
        // Act
        GameUserEntry result = GameMappers.GAME_USER_ENTRY_MAPPER.gameUserEntryDtoToGameUserEntry(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void gameUserEntryDtoToGameUserEntry_withGameUserEntryDto_mapsFields() {
        // Arrange
        GameUserEntryDto gameUserEntryDto = new GameUserEntryDto();
        gameUserEntryDto.setId(5L);
        gameUserEntryDto.setGameId(6L);
        gameUserEntryDto.setPlatformId(7L);
        gameUserEntryDto.setUserId(8L);
        gameUserEntryDto.setStatus(GameUserEntryStatus.DROPPED);
        gameUserEntryDto.setRating((short)5);
        gameUserEntryDto.setCreatedAt(LocalDateTime.now());
        gameUserEntryDto.setUpdatedAt(LocalDateTime.now());

        // Act
        GameUserEntry result = GameMappers.GAME_USER_ENTRY_MAPPER.gameUserEntryDtoToGameUserEntry(gameUserEntryDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameUserEntryDto.getId());
        Assertions.assertThat(result.getGameId()).isEqualTo(gameUserEntryDto.getGameId());
        Assertions.assertThat(result.getPlatformId()).isEqualTo(gameUserEntryDto.getPlatformId());
        Assertions.assertThat(result.getUserId()).isEqualTo(gameUserEntryDto.getUserId());
        Assertions.assertThat(result.getStatus()).isEqualTo(gameUserEntryDto.getStatus());
        Assertions.assertThat(result.getRating()).isEqualTo(gameUserEntryDto.getRating());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
    }
}
