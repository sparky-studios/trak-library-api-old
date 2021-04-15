package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDate;

@DataJpaTest
class GameBarcodeTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullGame_throwsPersistenceException() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("test-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = testEntityManager.persistFlushFind(platform);

        GameBarcode gameBarcode = new GameBarcode();
        gameBarcode.setPlatformId(platform.getId());
        gameBarcode.setBarcode("barcode");
        gameBarcode.setBarcodeType(BarcodeType.UPC_A);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameBarcode));
    }

    @Test
    void persist_withNullPlatform_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setSlug("test-slug");
        game = testEntityManager.persistFlushFind(game);

        GameBarcode gameBarcode = new GameBarcode();
        gameBarcode.setGameId(game.getId());
        gameBarcode.setBarcode("barcode");
        gameBarcode.setBarcodeType(BarcodeType.UPC_A);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameBarcode));
    }

    @Test
    void persist_withNullBarcode_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setSlug("test-slug");
        game = testEntityManager.persistFlushFind(game);

        Platform platform = new Platform();
        platform.setName("test-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = testEntityManager.persistFlushFind(platform);

        GameBarcode gameBarcode = new GameBarcode();
        gameBarcode.setGameId(game.getId());
        gameBarcode.setPlatformId(platform.getId());
        gameBarcode.setBarcode(null);
        gameBarcode.setBarcodeType(BarcodeType.UPC_A);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameBarcode));
    }

    @Test
    void persist_withNullBarcodeType_throwsPersistenceException() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setSlug("test-slug");
        game = testEntityManager.persistFlushFind(game);

        Platform platform = new Platform();
        platform.setName("test-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = testEntityManager.persistFlushFind(platform);

        GameBarcode gameBarcode = new GameBarcode();
        gameBarcode.setGameId(game.getId());
        gameBarcode.setPlatformId(platform.getId());
        gameBarcode.setBarcode("barcode");
        gameBarcode.setBarcodeType(null);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameBarcode));
    }

    @Test
    void persist_withValidGameBarcode_mapsGameBarcode() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setSlug("test-slug");
        game = testEntityManager.persistFlushFind(game);

        Platform platform = new Platform();
        platform.setName("test-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = testEntityManager.persistFlushFind(platform);

        GameBarcode gameBarcode = new GameBarcode();
        gameBarcode.setGameId(game.getId());
        gameBarcode.setPlatformId(platform.getId());
        gameBarcode.setBarcode("barcode");
        gameBarcode.setBarcodeType(BarcodeType.UPC_A);

        // Act
        GameBarcode result = testEntityManager.persistFlushFind(gameBarcode);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getGameId()).isEqualTo(game.getId());
        Assertions.assertThat(result.getGame().getId())
                .isEqualTo(result.getGame().getId());
        Assertions.assertThat(result.getPlatformId()).isEqualTo(platform.getId());
        Assertions.assertThat(result.getPlatform().getId())
                .isEqualTo(result.getPlatform().getId());
        Assertions.assertThat(result.getBarcode()).isEqualTo(result.getBarcode());
        Assertions.assertThat(result.getBarcodeType()).isEqualTo(result.getBarcodeType());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }
}
