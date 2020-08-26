package com.traklibrary.game.service.mapper;

import com.traklibrary.game.service.dto.GameInfoDto;
import com.traklibrary.game.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

class GameInfoMapperTest {

    @Test
    void gameToGameInfoDto_withNull_returnsNull() {
        // Act
        GameInfoDto result = GameMappers.GAME_INFO_MAPPER.gameToGameInfoDto(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    void gameToGameInfoDto_withGame_mapsFields() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("test-genre");

        Platform platform = new Platform();
        platform.setName("test-platform");

        Publisher publisher = new Publisher();
        publisher.setName("test-publisher");

        Game game = new Game();
        game.setId(5L);
        game.setTitle("test-title");
        game.setDescription("sure is a description.");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.TEEN);
        game.setVersion(5L);
        game.addGenre(genre);
        game.addPlatform(platform);
        game.addPublisher(publisher);

        // Act
        GameInfoDto result = GameMappers.GAME_INFO_MAPPER.gameToGameInfoDto(game);

        // Assert
        Assertions.assertEquals(game.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(game.getTitle(), result.getTitle(), "The mapped title does not match the entity.");
        Assertions.assertEquals(game.getDescription(), result.getDescription(), "The mapped description does not match the entity.");
        Assertions.assertEquals(game.getReleaseDate(), result.getReleaseDate(), "The mapped release date does not match the entity.");
        Assertions.assertEquals(game.getAgeRating(), result.getAgeRating(), "The mapped age rating does not match the entity.");
        Assertions.assertEquals(game.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
        Assertions.assertEquals(1, result.getGenres().size(), "Incorrect number of mapped genres.");
        Assertions.assertEquals(1, result.getPlatforms().size(), "Incorrect number of mapped platforms.");
        Assertions.assertEquals(1, result.getPublishers().size(), "Incorrect number of mapped publishers.");
    }
}
