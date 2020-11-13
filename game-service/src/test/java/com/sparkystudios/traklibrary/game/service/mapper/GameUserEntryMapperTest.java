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

        GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
        gameUserEntryPlatform.setId(1L);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setId(5L);
        gameUserEntry.setGameId(6L);
        gameUserEntry.setGame(game);
        gameUserEntry.setUserId(8L);
        gameUserEntry.setStatus(GameUserEntryStatus.DROPPED);
        gameUserEntry.setRating((short)5);
        gameUserEntry.setCreatedAt(LocalDateTime.now());
        gameUserEntry.setUpdatedAt(LocalDateTime.now());
        gameUserEntry.addGameUserEntryPlatform(gameUserEntryPlatform);

        // Act
        GameUserEntryDto result = GameMappers.GAME_USER_ENTRY_MAPPER.gameUserEntryToGameUserEntryDto(gameUserEntry);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameUserEntry.getId());
        Assertions.assertThat(result.getGameId()).isEqualTo(gameUserEntry.getGameId());
        Assertions.assertThat(result.getGameTitle()).isEqualTo(game.getTitle());
        Assertions.assertThat(result.getPublishers()).hasSize(1);
        Assertions.assertThat(result.getPublishers().iterator().next()).isEqualTo(publisher.getName());
        Assertions.assertThat(result.getUserId()).isEqualTo(gameUserEntry.getUserId());
        Assertions.assertThat(result.getStatus()).isEqualTo(gameUserEntry.getStatus());
        Assertions.assertThat(result.getRating()).isEqualTo(gameUserEntry.getRating());
        Assertions.assertThat(result.getGameUserEntryPlatforms()).hasSize(1);
        Assertions.assertThat(result.getGameUserEntryPlatforms().iterator().next().getId())
                .isEqualTo(gameUserEntryPlatform.getId());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(gameUserEntry.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(gameUserEntry.getUpdatedAt());
    }
}
