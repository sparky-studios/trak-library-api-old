package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameUserEntryPlatform;
import com.sparkystudios.traklibrary.game.domain.Platform;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryPlatformDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class GameUserEntryPlatformMapperTest {

    @Test
    void gameUserEntryPlatformToGameUserEntryPlatformDto_withNull_returnsNull() {
        // Act
        GameUserEntryPlatformDto result = GameMappers.GAME_USER_ENTRY_PLATFORM_MAPPER.gameUserEntryPlatformToGameUserEntryPlatformDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void gameUserEntryPlatformToGameUserEntryPlatformDto_withGameUserEntryPlatform_mapsFields() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("platform-name");
        GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
        gameUserEntryPlatform.setId(1L);
        gameUserEntryPlatform.setPlatformId(2L);
        gameUserEntryPlatform.setPlatform(platform);
        gameUserEntryPlatform.setGameUserEntryId(3L);
        gameUserEntryPlatform.setCreatedAt(LocalDateTime.now());
        gameUserEntryPlatform.setUpdatedAt(LocalDateTime.now());
        gameUserEntryPlatform.setVersion(4L);

        // Act
        GameUserEntryPlatformDto result = GameMappers.GAME_USER_ENTRY_PLATFORM_MAPPER
                .gameUserEntryPlatformToGameUserEntryPlatformDto(gameUserEntryPlatform);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameUserEntryPlatform.getId());
        Assertions.assertThat(result.getPlatformId()).isEqualTo(gameUserEntryPlatform.getPlatformId());
        Assertions.assertThat(result.getPlatformName()).isEqualTo(platform.getName());
        Assertions.assertThat(result.getGameUserEntryId()).isEqualTo(gameUserEntryPlatform.getGameUserEntryId());
        Assertions.assertThat(result.getCreatedAt())
                .isEqualToIgnoringNanos(gameUserEntryPlatform.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt())
                .isEqualToIgnoringNanos(gameUserEntryPlatform.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(gameUserEntryPlatform.getVersion());
    }
}
