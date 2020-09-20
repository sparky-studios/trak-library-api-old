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
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game = testEntityManager.persistFlushFind(game);

        GameImage gameImage = new GameImage();
        gameImage.setGameId(game.getId());
        gameImage.setFilename("filename.png");

        // Act
        GameImage result = testEntityManager.persistFlushFind(gameImage);

        // Assert
        Assertions.assertThat(result.getId()).isGreaterThan(0L);
        Assertions.assertThat(result.getGameId()).isEqualTo(game.getId());
        Assertions.assertThat(result.getGame()).isEqualTo(result.getGame());
        Assertions.assertThat(result.getFilename()).isEqualTo(gameImage.getFilename());
        Assertions.assertThat(result.getVersion()).isNotNull().isGreaterThanOrEqualTo(0L);
    }
}
