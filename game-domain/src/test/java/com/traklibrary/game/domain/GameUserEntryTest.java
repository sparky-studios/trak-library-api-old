package com.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDate;

@DataJpaTest
class GameUserEntryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullGame_throwsPersistenceException() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("test-platform");
        platform.setDescription("test-description");
        platform.setReleaseDate(LocalDate.now());
        platform = testEntityManager.persistFlushFind(platform);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setPlatformId(platform.getId());
        gameUserEntry.setUserId(1L);
        gameUserEntry.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntry.setRating((short)5);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameUserEntry));
    }

    @Test
    void persist_withNullPlatform_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game = testEntityManager.persistFlushFind(game);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.setUserId(1L);
        gameUserEntry.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntry.setRating((short)5);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameUserEntry));
    }

    @Test
    void persist_withNullStatus_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game = testEntityManager.persistFlushFind(game);

        Platform platform = new Platform();
        platform.setName("test-platform");
        platform.setDescription("test-description");
        platform.setReleaseDate(LocalDate.now());
        platform = testEntityManager.persistFlushFind(platform);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.setPlatformId(platform.getId());
        gameUserEntry.setUserId(1L);
        gameUserEntry.setStatus(null);
        gameUserEntry.setRating((short)5);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameUserEntry));
    }

    @Test
    void persist_withValidGameUserEntry_mapsGameUserEntry() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game = testEntityManager.persistFlushFind(game);

        Platform platform = new Platform();
        platform.setName("test-platform");
        platform.setDescription("test-description");
        platform.setReleaseDate(LocalDate.now());
        platform = testEntityManager.persistFlushFind(platform);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.setPlatformId(platform.getId());
        gameUserEntry.setUserId(1L);
        gameUserEntry.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntry.setRating((short)5);

        // Act
        GameUserEntry result = testEntityManager.persistFlushFind(gameUserEntry);

        // Assert
        Assertions.assertThat(result.getId()).isGreaterThan(0L);
        Assertions.assertThat(result.getGameId()).isEqualTo(gameUserEntry.getGameId());
        Assertions.assertThat(result.getGame()).isEqualTo(game);
        Assertions.assertThat(result.getPlatformId()).isEqualTo(gameUserEntry.getPlatformId());
        Assertions.assertThat(result.getPlatform()).isEqualTo(platform);
        Assertions.assertThat(result.getUserId()).isEqualTo(gameUserEntry.getUserId());
        Assertions.assertThat(result.getStatus()).isEqualTo(gameUserEntry.getStatus());
        Assertions.assertThat(result.getRating()).isEqualTo(gameUserEntry.getRating());
        Assertions.assertThat(result.getVersion()).isNotNull().isGreaterThanOrEqualTo(0L);
    }
}
