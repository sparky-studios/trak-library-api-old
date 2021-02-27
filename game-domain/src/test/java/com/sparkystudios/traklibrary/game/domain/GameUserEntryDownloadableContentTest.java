package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDate;

@DataJpaTest
class GameUserEntryDownloadableContentTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullGameUserEntry_throwsPersistenceException() {
        // Arrange
        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName("test-name-1");
        downloadableContent.setDescription("test-description-1");
        downloadableContent.setReleaseDate(LocalDate.now());

        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.addDownloadableContent(downloadableContent);
        game = testEntityManager.persistFlushFind(game);

        GameUserEntryDownloadableContent gameUserEntryDownloadableContent = new GameUserEntryDownloadableContent();
        gameUserEntryDownloadableContent.setGameUserEntry(null);
        gameUserEntryDownloadableContent.setDownloadableContent(game.getDownloadableContents().iterator().next());

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameUserEntryDownloadableContent));
    }

    @Test
    void persist_withNullDownloadableContent_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game = testEntityManager.persistFlushFind(game);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.setUserId(1L);
        gameUserEntry.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntry.setRating((short)5);
        gameUserEntry = testEntityManager.persistFlushFind(gameUserEntry);

        GameUserEntryDownloadableContent gameUserEntryDownloadableContent = new GameUserEntryDownloadableContent();
        gameUserEntryDownloadableContent.setGameUserEntry(gameUserEntry);
        gameUserEntryDownloadableContent.setDownloadableContent(null);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameUserEntryDownloadableContent));
    }

    @Test
    void persist_withValidGameUserEntryPlatform_mapsGameUserEntryPlatform() {
        // Arrange
        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName("test-name-1");
        downloadableContent.setDescription("test-description-1");
        downloadableContent.setReleaseDate(LocalDate.now());

        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.addDownloadableContent(downloadableContent);
        game = testEntityManager.persistFlushFind(game);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.setUserId(1L);
        gameUserEntry.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntry.setRating((short)5);
        gameUserEntry = testEntityManager.persistFlushFind(gameUserEntry);

        GameUserEntryDownloadableContent gameUserEntryDownloadableContent = new GameUserEntryDownloadableContent();
        gameUserEntryDownloadableContent.setGameUserEntry(gameUserEntry);
        gameUserEntryDownloadableContent.setDownloadableContent(game.getDownloadableContents().iterator().next());

        // Act
        GameUserEntryDownloadableContent result = testEntityManager.persistFlushFind(gameUserEntryDownloadableContent);

        // Assert
        DownloadableContent dc = game.getDownloadableContents().iterator().next();

        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getGameUserEntryId()).isEqualTo(gameUserEntry.getId());
        Assertions.assertThat(result.getGameUserEntry().getId()).isEqualTo(gameUserEntry.getId());
        Assertions.assertThat(result.getDownloadableContentId()).isEqualTo(dc.getId());
        Assertions.assertThat(result.getDownloadableContent().getId()).isEqualTo(dc.getId());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }
}
