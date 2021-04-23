package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDate;

@DataJpaTest
class GameImageTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullGame_throwsPersistenceException() {
        // Arrange
        GameImage gameImage = new GameImage();
        gameImage.setFilename("filename.png");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameImage));
    }

    @Test
    void persist_withValidGameImage_mapsGameImage() {
        // Arrange
        Game game = new Game();
        game.setTitle("game-title");
        game.setDescription("game-description");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setSlug("test-slug");
        game = testEntityManager.persistFlushFind(game);

        GameImage gameImage = new GameImage();
        gameImage.setGameId(game.getId());
        gameImage.setFilename("filename.png");
        gameImage.setImageSize(GameImageSize.MEDIUM);

        // Act
        GameImage result = testEntityManager.persistFlushFind(gameImage);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getGameId()).isEqualTo(game.getId());
        Assertions.assertThat(result.getGame().getId())
                .isEqualTo(result.getGame().getId());
        Assertions.assertThat(result.getFilename()).isEqualTo(gameImage.getFilename());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }
}
