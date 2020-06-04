package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.*;
import com.sparky.trak.game.service.dto.GameInfoDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

public class GameInfoMapperTest {

    @Test
    public void gameToGameInfoDto_withGame_mapsFields() {
        // Arrange
        Game game = new Game();
        game.setId(5L);
        game.setTitle("test-title");
        game.setDescription("sure is a description.");
        game.setReleaseDate(LocalDate.now());
        game.setAgeRating(AgeRating.TEEN);
        game.setVersion(5L);

        Genre genre = new Genre();
        genre.setName("test-genre");

        GameGenreXref gameGenreXref = new GameGenreXref();
        gameGenreXref.setGenre(genre);

        game.setGameGenreXrefs(Collections.singleton(gameGenreXref));

        Platform platform = new Platform();
        platform.setName("test-platform");

        GamePlatformXref gamePlatformXref = new GamePlatformXref();
        gamePlatformXref.setPlatform(platform);

        game.setGamePlatformXrefs(Collections.singleton(gamePlatformXref));

        Publisher publisher = new Publisher();
        publisher.setName("test-publisher");

        GamePublisherXref gamePublisherXref = new GamePublisherXref();
        gamePublisherXref.setPublisher(publisher);

        game.setGamePublisherXrefs(Collections.singleton(gamePublisherXref));

        // Act
        GameInfoDto result = GameInfoMapper.INSTANCE.gameToGameInfoDto(game);

        // Assert
        Assertions.assertEquals(game.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(game.getTitle(), result.getTitle(), "The mapped title does not match the entity.");
        Assertions.assertEquals(game.getDescription(), result.getDescription(), "The mapped description does not match the entity.");
        Assertions.assertEquals(game.getReleaseDate(), result.getReleaseDate(), "The mapped release date does not match the entity.");
        Assertions.assertEquals(game.getAgeRating(), result.getAgeRating(), "The mapped age rating does not match the entity.");
        Assertions.assertEquals(game.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
        Assertions.assertEquals(genre.getName(), result.getGenres().iterator().next(), "The mapped genres does not match the entity.");
        Assertions.assertEquals(platform.getName(), result.getPlatforms().iterator().next(), "The mapped platforms does not match the entity.");
        Assertions.assertEquals(publisher.getName(), result.getPublishers().iterator().next(), "The mapped publishers does not match the entity.");
    }
}
