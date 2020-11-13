package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;

@DataJpaTest
class GameUserEntryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullGame_throwsPersistenceException() {
        // Arrange
        GameUserEntry gameUserEntry = new GameUserEntry();
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
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game = testEntityManager.persistFlushFind(game);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.setUserId(1L);
        gameUserEntry.setStatus(null);
        gameUserEntry.setRating((short)5);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameUserEntry));
    }

    @Test
    void persist_withValidGameUserEntryPlatformRelationships_mapsRelationship() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game = testEntityManager.persistFlushFind(game);

        Platform platform1 = new Platform();
        platform1.setName("test-platform-1");
        platform1 = testEntityManager.persistFlushFind(platform1);

        Platform platform2 = new Platform();
        platform2.setName("test-platform-2");
        platform2 = testEntityManager.persistFlushFind(platform2);

        GameUserEntryPlatform gameUserEntryPlatform1 = new GameUserEntryPlatform();
        gameUserEntryPlatform1.setPlatform(platform1);

        GameUserEntryPlatform gameUserEntryPlatform2 = new GameUserEntryPlatform();
        gameUserEntryPlatform2.setPlatform(platform2);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.setUserId(1L);
        gameUserEntry.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntry.setRating((short)5);
        gameUserEntry.addGameUserEntryPlatform(gameUserEntryPlatform1);
        gameUserEntry.addGameUserEntryPlatform(gameUserEntryPlatform2);

        // Act
        GameUserEntry result = testEntityManager.persistFlushFind(gameUserEntry);

        // Assert
        Assertions.assertThat(result.getGameUserEntryPlatforms()).hasSize(2);
    }
}
