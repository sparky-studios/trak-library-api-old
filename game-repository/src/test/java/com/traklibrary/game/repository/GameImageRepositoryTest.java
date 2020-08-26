package com.traklibrary.game.repository;

import com.traklibrary.game.domain.AgeRating;
import com.traklibrary.game.domain.Game;
import com.traklibrary.game.domain.GameImage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
class GameImageRepositoryTest {

    @Autowired
    private GameImageRepository gameImageRepository;

    @Autowired
    private GameRepository gameRepository;

    @Test
    void existsByGameId_withNonExistentGame_returnsFalse() {
        // Act
        boolean result = gameImageRepository.existsByGameId(1L);

        // Assert
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void existsByGameId_withGame_returnsTrue() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game = gameRepository.save(game);

        GameImage gameImage = new GameImage();
        gameImage.setGameId(game.getId());
        gameImage.setFilename("filename.png");
        gameImageRepository.save(gameImage);

        // Act
        boolean result = gameImageRepository.existsByGameId(game.getId());

        // Assert
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void findByGameId_withNonExistentGameImage_returnsEmptyOptional() {
        // Act
        Optional<GameImage> result = gameImageRepository.findByGameId(1L);

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByGameId_withGameImage_returnsGameImage() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game = gameRepository.save(game);

        GameImage gameImage = new GameImage();
        gameImage.setGameId(game.getId());
        gameImage.setFilename("filename.png");
        gameImageRepository.save(gameImage);

        // Act
        Optional<GameImage> result = gameImageRepository.findByGameId(game.getId());

        // Assert
        Assertions.assertThat(result).isNotEmpty();
    }
}
