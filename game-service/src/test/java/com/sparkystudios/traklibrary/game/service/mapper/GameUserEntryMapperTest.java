package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        GameUserEntryMapperImpl.class,
        GameUserEntryPlatformMapperImpl.class,
        GameUserEntryDownloadableContentMapperImpl.class
})
class GameUserEntryMapperTest {

    @Autowired
    private GameUserEntryMapper gameUserEntryMapper;

    @Test
    void fromGameUserEntry_withNull_returnsNull() {
        // Act
        GameUserEntryDto result = gameUserEntryMapper.fromGameUserEntry(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void fromGameUserEntry_withGameUserEntry_mapsFields() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setName("publisher-name");

        Game game = new Game();
        game.setTitle("test-title");
        game.addPublisher(publisher);


        GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
        gameUserEntryPlatform.setId(1L);

        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName("test-name");

        GameUserEntryDownloadableContent gameUserEntryDownloadableContent = new GameUserEntryDownloadableContent();
        gameUserEntryDownloadableContent.setId(2L);
        gameUserEntryDownloadableContent.setDownloadableContent(downloadableContent);

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
        gameUserEntry.addGameUserEntryDownloadableContent(gameUserEntryDownloadableContent);

        // Act
        GameUserEntryDto result = gameUserEntryMapper.fromGameUserEntry(gameUserEntry);

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
        Assertions.assertThat(result.getGameUserEntryDownloadableContents()).hasSize(1);
        Assertions.assertThat(result.getGameUserEntryDownloadableContents().iterator().next().getId())
                .isEqualTo(gameUserEntryDownloadableContent.getId());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(gameUserEntry.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(gameUserEntry.getUpdatedAt());
    }
}
