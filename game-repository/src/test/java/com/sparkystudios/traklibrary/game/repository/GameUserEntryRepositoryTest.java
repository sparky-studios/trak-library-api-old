package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.repository.specification.GameUserEntrySearchSpecification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collection;
import java.util.Collections;

@DataJpaTest
class GameUserEntryRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private GameUserEntryRepository gameUserEntryRepository;

    @Test
    void findAllWithGameUserEntrySearchSpecification_withNoMatchingGameUserEntryPlatforms_returnsEmpty() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("genre-1");
        genre.setDescription("description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("title");
        game.setDescription("description");
        game.setAgeRating(AgeRating.EVERYONE);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addGenre(genre);
        game = gameRepository.save(game);

        Platform platform1 = new Platform();
        platform1.setName("platform-1");
        platform1.setDescription("description-1");
        platform1.setSlug("test-slug-1");
        platform1 = platformRepository.save(platform1);

        Platform platform2 = new Platform();
        platform2.setName("platform-1");
        platform2.setDescription("description-1");
        platform2.setSlug("test-slug-2");
        platform2 = platformRepository.save(platform2);

        GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
        gameUserEntryPlatform.setPlatform(platform1);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setUserId(1L);
        gameUserEntry.setRating((short)3);
        gameUserEntry.setStatus(GameUserEntryStatus.BACKLOG);
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.addGameUserEntryPlatform(gameUserEntryPlatform);
        gameUserEntryRepository.save(gameUserEntry);

        // Act
        Collection<GameUserEntry> result = gameUserEntryRepository
                .findAll(new GameUserEntrySearchSpecification(Collections.singleton(platform2), Collections.singleton(genre), game.getGameModes(), Collections.singleton(game.getAgeRating()), Collections.singleton(gameUserEntry.getStatus())));

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findAllWithGameUserEntrySearchSpecification_withNoMatchingGenres_returnsEmpty() {
        // Arrange
        Genre genre1 = new Genre();
        genre1.setName("genre-1");
        genre1.setDescription("description");
        genre1.setSlug("test-slug-1");
        genre1 = genreRepository.save(genre1);

        Genre genre2 = new Genre();
        genre2.setName("genre-2");
        genre2.setDescription("description");
        genre2.setSlug("test-slug-2");
        genre2 = genreRepository.save(genre2);

        Game game = new Game();
        game.setTitle("title");
        game.setDescription("description");
        game.setAgeRating(AgeRating.EVERYONE);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addGenre(genre1);
        game = gameRepository.save(game);

        Platform platform = new Platform();
        platform.setName("platform-1");
        platform.setDescription("description-1");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
        gameUserEntryPlatform.setPlatform(platform);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setUserId(1L);
        gameUserEntry.setRating((short)3);
        gameUserEntry.setStatus(GameUserEntryStatus.BACKLOG);
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.addGameUserEntryPlatform(gameUserEntryPlatform);
        gameUserEntryRepository.save(gameUserEntry);

        // Act
        Collection<GameUserEntry> result = gameUserEntryRepository
                .findAll(new GameUserEntrySearchSpecification(Collections.singleton(platform), Collections.singleton(genre2), game.getGameModes(), Collections.singleton(game.getAgeRating()), Collections.singleton(gameUserEntry.getStatus())));

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findAllWithGameUserEntrySearchSpecification_withNoMatchingGameModes_returnsEmpty() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("genre-1");
        genre.setDescription("description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("title");
        game.setDescription("description");
        game.setAgeRating(AgeRating.EVERYONE);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addGenre(genre);
        game = gameRepository.save(game);

        Platform platform = new Platform();
        platform.setName("platform-1");
        platform.setDescription("description-1");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
        gameUserEntryPlatform.setPlatform(platform);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setUserId(1L);
        gameUserEntry.setRating((short)3);
        gameUserEntry.setStatus(GameUserEntryStatus.BACKLOG);
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.addGameUserEntryPlatform(gameUserEntryPlatform);
        gameUserEntryRepository.save(gameUserEntry);

        // Act
        Collection<GameUserEntry> result = gameUserEntryRepository
                .findAll(new GameUserEntrySearchSpecification(Collections.singleton(platform), Collections.singleton(genre), Collections.singleton(GameMode.MULTI_PLAYER), Collections.singleton(game.getAgeRating()), Collections.singleton(gameUserEntry.getStatus())));

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findAllWithGameUserEntrySearchSpecification_withNoMatchingAgeRatings_returnsEmpty() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("genre-1");
        genre.setDescription("description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("title");
        game.setDescription("description");
        game.setAgeRating(AgeRating.EVERYONE);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addGenre(genre);
        game = gameRepository.save(game);

        Platform platform = new Platform();
        platform.setName("platform-1");
        platform.setDescription("description-1");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
        gameUserEntryPlatform.setPlatform(platform);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setUserId(1L);
        gameUserEntry.setRating((short)3);
        gameUserEntry.setStatus(GameUserEntryStatus.BACKLOG);
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.addGameUserEntryPlatform(gameUserEntryPlatform);
        gameUserEntryRepository.save(gameUserEntry);

        // Act
        Collection<GameUserEntry> result = gameUserEntryRepository
                .findAll(new GameUserEntrySearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes(), Collections.singleton(AgeRating.ADULTS_ONLY), Collections.singleton(gameUserEntry.getStatus())));

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findAllWithGameUserEntrySearchSpecification_withNoMatchingStatuses_returnsEmpty() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("genre-1");
        genre.setDescription("description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("title");
        game.setDescription("description");
        game.setAgeRating(AgeRating.EVERYONE);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addGenre(genre);
        game = gameRepository.save(game);

        Platform platform = new Platform();
        platform.setName("platform-1");
        platform.setDescription("description-1");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
        gameUserEntryPlatform.setPlatform(platform);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setUserId(1L);
        gameUserEntry.setRating((short)3);
        gameUserEntry.setStatus(GameUserEntryStatus.BACKLOG);
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.addGameUserEntryPlatform(gameUserEntryPlatform);
        gameUserEntryRepository.save(gameUserEntry);

        // Act
        Collection<GameUserEntry> result = gameUserEntryRepository
                .findAll(new GameUserEntrySearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes(), Collections.singleton(game.getAgeRating()), Collections.singleton(GameUserEntryStatus.COMPLETED)));

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findAllWithGameUserEntrySearchSpecification_withMatchingCriteria_returnsResults() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("genre-1");
        genre.setDescription("description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("title");
        game.setDescription("description");
        game.setAgeRating(AgeRating.EVERYONE);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addGenre(genre);
        game = gameRepository.save(game);

        Platform platform = new Platform();
        platform.setName("platform-1");
        platform.setDescription("description-1");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
        gameUserEntryPlatform.setPlatform(platform);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setUserId(1L);
        gameUserEntry.setRating((short)3);
        gameUserEntry.setStatus(GameUserEntryStatus.BACKLOG);
        gameUserEntry.setGameId(game.getId());
        gameUserEntry.addGameUserEntryPlatform(gameUserEntryPlatform);
        gameUserEntryRepository.save(gameUserEntry);

        // Act
        Collection<GameUserEntry> result = gameUserEntryRepository
                .findAll(new GameUserEntrySearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes(), Collections.singleton(game.getAgeRating()), Collections.singleton(gameUserEntry.getStatus())));

        // Assert
        Assertions.assertThat(result).isNotEmpty();
    }
}
