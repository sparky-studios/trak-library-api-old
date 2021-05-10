package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.GameImage;
import com.sparkystudios.traklibrary.game.domain.ImageSize;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class GameImageRepositoryTest {

    @Autowired
    private GameImageRepository gameImageRepository;

    @Autowired
    private GameRepository gameRepository;

    @Test
    void existsByGameIdAndImageSize_withNonExistentGameAndGameImageSize_returnsFalse() {
        // Act
        boolean result = gameImageRepository.existsByGameIdAndImageSize(1L, ImageSize.SMALL);

        // Assert
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void existsByGameIdAndImageSize_withGameAndGameImageSize_returnsTrue() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setSlug("test-slug");
        game = gameRepository.save(game);

        GameImage gameImage = new GameImage();
        gameImage.setGameId(game.getId());
        gameImage.setFilename("filename.png");
        gameImage.setImageSize(ImageSize.MEDIUM);
        gameImageRepository.save(gameImage);

        // Act
        boolean result = gameImageRepository.existsByGameIdAndImageSize(game.getId(), ImageSize.MEDIUM);

        // Assert
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void findByGameIdAndImageSize_withNonExistentGameImage_returnsEmptyOptional() {
        // Act
        Optional<GameImage> result = gameImageRepository.findByGameIdAndImageSize(1L, ImageSize.SMALL);

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByGameIdAndImageSize_withGameImage_returnsGameImage() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setSlug("test-slug");
        game = gameRepository.save(game);

        GameImage gameImage = new GameImage();
        gameImage.setGameId(game.getId());
        gameImage.setFilename("filename.png");
        gameImage.setImageSize(ImageSize.SMALL);
        gameImageRepository.save(gameImage);

        // Act
        Optional<GameImage> result = gameImageRepository.findByGameIdAndImageSize(game.getId(), ImageSize.SMALL);

        // Assert
        Assertions.assertThat(result).isNotEmpty();
    }
}
