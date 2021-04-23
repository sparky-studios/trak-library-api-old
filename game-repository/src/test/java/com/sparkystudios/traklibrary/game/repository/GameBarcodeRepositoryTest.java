package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
class GameBarcodeRepositoryTest {

    @Autowired
    private GameBarcodeRepository gameBarcodeRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Test
    void findByBarcode_withNonExistentGameBarcode_returnsEmptyOptional() {
        // Act
        Optional<GameBarcode> result = gameBarcodeRepository.findByBarcode("barcode");

        // Assert
        Assertions.assertThat(result).isNotPresent();
    }


    @Test
    void findByBarcode_withGameBarcode_returnsGameBarcode() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setSlug("test-slug");
        game = gameRepository.save(game);

        Platform platform = new Platform();
        platform.setName("test-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        GameBarcode gameBarcode = new GameBarcode();
        gameBarcode.setGameId(game.getId());
        gameBarcode.setPlatformId(platform.getId());
        gameBarcode.setBarcode("barcode");
        gameBarcode.setBarcodeType(BarcodeType.EAN_13);
        gameBarcode = gameBarcodeRepository.save(gameBarcode);

        // Act
        Optional<GameBarcode> result = gameBarcodeRepository.findByBarcode("barcode");

        // Assert
        Assertions.assertThat(result).isPresent()
                .isEqualTo(Optional.of(gameBarcode));
    }

}
