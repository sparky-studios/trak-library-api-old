package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.service.dto.GameDetailsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class GameDetailsMapperTest {

    @Test
    void gameToGameDetailsDto_withNull_returnsNull() {
        // Act
        GameDetailsDto result = GameMappers.GAME_INFO_MAPPER.gameToGameDetailsDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void gameToGameDetailsDto_withGame_mapsFields() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("test-genre");

        Platform platform = new Platform();
        platform.setName("test-platform");

        Publisher publisher = new Publisher();
        publisher.setName("test-publisher");

        GameReleaseDate gameReleaseDate = new GameReleaseDate();
        gameReleaseDate.setRegion(GameRegion.PAL);
        gameReleaseDate.setReleaseDate(LocalDate.now());
        gameReleaseDate.setVersion(1L);

        Franchise franchise = new Franchise();
        franchise.setTitle("franchise-title");

        Game game = new Game();
        game.setId(5L);
        game.setTitle("test-title");
        game.setDescription("sure is a description.");
        game.setAgeRating(AgeRating.TEEN);
        game.setFranchiseId(5L);
        game.setVersion(5L);
        game.addGenre(genre);
        game.addPlatform(platform);
        game.addPublisher(publisher);
        game.addReleaseDate(gameReleaseDate);
        game.setFranchise(franchise);

        // Act
        GameDetailsDto result = GameMappers.GAME_INFO_MAPPER.gameToGameDetailsDto(game);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(game.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(game.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(game.getDescription());
        Assertions.assertThat(result.getAgeRating()).isEqualTo(game.getAgeRating());
        Assertions.assertThat(result.getFranchiseId()).isEqualTo(game.getFranchiseId());
        Assertions.assertThat(result.getVersion()).isEqualTo(game.getVersion());
        Assertions.assertThat(result.getGenres()).hasSize(1);
        Assertions.assertThat(result.getPlatforms()).hasSize(1);
        Assertions.assertThat(result.getPublishers()).hasSize(1);
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
        Assertions.assertThat(result.getFranchise().getTitle()).isEqualTo(franchise.getTitle());
    }
}
