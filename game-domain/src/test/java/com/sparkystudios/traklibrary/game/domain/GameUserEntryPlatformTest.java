package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;

@DataJpaTest
class GameUserEntryPlatformTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullGameUserEntry_throwsPersistenceException() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("test-platform");
        platform = testEntityManager.persistFlushFind(platform);

        GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
        gameUserEntryPlatform.setGameUserEntry(null);
        gameUserEntryPlatform.setPlatform(platform);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameUserEntryPlatform));
    }

    @Test
    void persist_withNullPlatform_throwsPersistenceException() {
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

        GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
        gameUserEntryPlatform.setGameUserEntry(gameUserEntry);
        gameUserEntryPlatform.setPlatform(null);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameUserEntryPlatform));
    }

    @Test
    void persist_withValidGameUserEntryPlatform_mapsGameUserEntryPlatform() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game = testEntityManager.persistFlushFind(game);

        Platform platform = new Platform();
        platform.setName("test-platform-1");
        platform = testEntityManager.persistFlushFind(platform);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.setUserId(1L);
        gameUserEntry.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntry.setRating((short)5);
        gameUserEntry = testEntityManager.persistFlushFind(gameUserEntry);

        GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
        gameUserEntryPlatform.setGameUserEntry(gameUserEntry);
        gameUserEntryPlatform.setPlatform(platform);

        // Act
        GameUserEntryPlatform result = testEntityManager.persistFlushFind(gameUserEntryPlatform);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getGameUserEntryId()).isEqualTo(gameUserEntry.getId());
        Assertions.assertThat(result.getGameUserEntry().getId()).isEqualTo(gameUserEntry.getId());
        Assertions.assertThat(result.getPlatformId()).isEqualTo(platform.getId());
        Assertions.assertThat(result.getPlatform().getId()).isEqualTo(platform.getId());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }
}
