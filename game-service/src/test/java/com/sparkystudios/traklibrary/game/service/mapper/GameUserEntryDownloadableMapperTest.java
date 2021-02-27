package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.DownloadableContent;
import com.sparkystudios.traklibrary.game.domain.GameUserEntryDownloadableContent;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDownloadableContentDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class GameUserEntryDownloadableMapperTest {

    @Test
    void gameUserEntryDownloadableContentToGameUserEntryDownloadableContentDto_withNull_returnsNull() {
        // Act
        GameUserEntryDownloadableContentDto result = GameMappers.GAME_USER_ENTRY_DOWNLOADABLE_CONTENT_MAPPER
                .gameUserEntryDownloadableContentToGameUserEntryDownloadableContentDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void gameUserEntryPlatformToGameUserEntryPlatformDto_withGameUserEntryPlatform_mapsFields() {
        // Arrange
        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName("dlc-name");

        GameUserEntryDownloadableContent gameUserEntryDownloadableContent = new GameUserEntryDownloadableContent();
        gameUserEntryDownloadableContent.setId(1L);
        gameUserEntryDownloadableContent.setDownloadableContentId(2L);
        gameUserEntryDownloadableContent.setDownloadableContent(downloadableContent);
        gameUserEntryDownloadableContent.setGameUserEntryId(3L);
        gameUserEntryDownloadableContent.setCreatedAt(LocalDateTime.now());
        gameUserEntryDownloadableContent.setUpdatedAt(LocalDateTime.now());
        gameUserEntryDownloadableContent.setVersion(4L);

        // Act
        GameUserEntryDownloadableContentDto result = GameMappers.GAME_USER_ENTRY_DOWNLOADABLE_CONTENT_MAPPER
                .gameUserEntryDownloadableContentToGameUserEntryDownloadableContentDto(gameUserEntryDownloadableContent);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameUserEntryDownloadableContent.getId());
        Assertions.assertThat(result.getDownloadableContentId()).isEqualTo(gameUserEntryDownloadableContent.getDownloadableContentId());
        Assertions.assertThat(result.getDownloadableContentName()).isEqualTo(downloadableContent.getName());
        Assertions.assertThat(result.getGameUserEntryId()).isEqualTo(gameUserEntryDownloadableContent.getGameUserEntryId());
        Assertions.assertThat(result.getCreatedAt())
                .isEqualToIgnoringNanos(gameUserEntryDownloadableContent.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt())
                .isEqualToIgnoringNanos(gameUserEntryDownloadableContent.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(gameUserEntryDownloadableContent.getVersion());
    }
}
