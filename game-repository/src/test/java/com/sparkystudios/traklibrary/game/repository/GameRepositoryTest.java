package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.repository.specification.GameSearchSpecification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class GameRepositoryTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Test
    void findByDevelopersId_withNoGames_returnsEmptyPage() {
        // Act
        Page<Game> result = gameRepository.findByDevelopersId(1L, Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByDevelopersId_withGames_returnsPage() {
        // Arrange
        Developer developer = new Developer();
        developer.setName("test-developer");
        developer.setDescription("test-description");
        developer.setFoundedDate(LocalDate.now());
        developer.setSlug("test-slug");
        developer = developerRepository.save(developer);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.addDeveloper(developer);
        game1.setSlug("test-slug-1");
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.addDeveloper(developer);
        game2.setSlug("test-slug-2");
        gameRepository.save(game2);

        // Act
        Page<Game> result = gameRepository.findByDevelopersId(developer.getId(), Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).hasSize(2);
    }

    @Test
    void countByDevelopersId_withNoGames_returnsZero() {
        // Act
        long result = gameRepository.countByDevelopersId(1L);

        // Assert
        Assertions.assertThat(result).isZero();
    }

    @Test
    void countByDevelopersId_withGames_returnsGameCount() {
        // Arrange
        Developer developer = new Developer();
        developer.setName("test-developer");
        developer.setDescription("test-description");
        developer.setFoundedDate(LocalDate.now());
        developer.setSlug("test-slug");
        developer = developerRepository.save(developer);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.addDeveloper(developer);
        game1.setSlug("test-slug-1");
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.addDeveloper(developer);
        game2.setSlug("test-slug-2");
        gameRepository.save(game2);

        // Act
        long result = gameRepository.countByDevelopersId(developer.getId());

        // Assert
        Assertions.assertThat(result).isEqualTo(2L);
    }

    @Test
    void findByPublishersId_withNoGames_returnsEmptyPage() {
        // Act
        Page<Game> result = gameRepository.findByPublishersId(1L, Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByPublishersId_withGames_returnsPage() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setName("test-developer");
        publisher.setDescription("test-description");
        publisher.setFoundedDate(LocalDate.now());
        publisher.setSlug("test-slug");
        publisher = publisherRepository.save(publisher);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.addPublisher(publisher);
        game1.setSlug("test-slug-1");
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.addPublisher(publisher);
        game2.setSlug("test-slug-2");
        gameRepository.save(game2);

        // Act
        Page<Game> result = gameRepository.findByPublishersId(publisher.getId(), Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).hasSize(2);
    }

    @Test
    void countByPublishersId_withNoGames_returnsZero() {
        // Act
        long result = gameRepository.countByPublishersId(1L);

        // Assert
        Assertions.assertThat(result).isZero();
    }

    @Test
    void countByPublishersId_withGames_returnsGameCount() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setName("test-developer");
        publisher.setDescription("test-description");
        publisher.setFoundedDate(LocalDate.now());
        publisher.setSlug("test-slug");
        publisher = publisherRepository.save(publisher);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.addPublisher(publisher);
        game1.setSlug("test-slug-1");
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.addPublisher(publisher);
        game2.setSlug("test-slug-2");
        gameRepository.save(game2);

        // Act
        long result = gameRepository.countByPublishersId(publisher.getId());

        // Assert
        Assertions.assertThat(result).isEqualTo(2L);
    }

    @Test
    void findByGenresId_withNoGames_returnsEmptyPage() {
        // Act
        Page<Game> result = gameRepository.findByGenresId(1L, Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByGenresId_withGames_returnsPage() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("test-genre");
        genre.setDescription("test-description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setSlug("test-slug-1");
        game1.addGenre(genre);
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setSlug("test-slug-2");
        game2.addGenre(genre);
        gameRepository.save(game2);

        // Act
        Page<Game> result = gameRepository.findByGenresId(genre.getId(), Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).hasSize(2);
    }

    @Test
    void countByGenresId_withNoGames_returnsZero() {
        // Act
        long result = gameRepository.countByGenresId(1L);

        // Assert
        Assertions.assertThat(result).isZero();
    }

    @Test
    void countByGenresId_withGames_returnsGameCount() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("test-genre");
        genre.setDescription("test-description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setSlug("test-slug-1");
        game1.addGenre(genre);
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setSlug("test-slug-2");
        game2.addGenre(genre);
        gameRepository.save(game2);

        // Act
        long result = gameRepository.countByGenresId(genre.getId());

        // Assert
        Assertions.assertThat(result).isEqualTo(2L);
    }

    @Test
    void findByPlatformsId_withNoGames_returnsEmptyPage() {
        // Act
        Page<Game> result = gameRepository.findByPlatformsId(1L, Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByPlatformsId_withGames_returnsPage() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("test-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setSlug("test-slug-1");
        game1.addPlatform(platform);
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setSlug("test-slug-2");
        game2.addPlatform(platform);
        gameRepository.save(game2);

        // Act
        Page<Game> result = gameRepository.findByPlatformsId(platform.getId(), Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).hasSize(2);
    }

    @Test
    void countByPlatformsId_withNoGames_returnsZero() {
        // Act
        long result = gameRepository.countByPlatformsId(1L);

        // Assert
        Assertions.assertThat(result).isZero();
    }

    @Test
    void countByPlatformsId_withGames_returnsGameCount() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("test-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setSlug("test-slug-1");
        game1.addPlatform(platform);
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setSlug("test-slug-2");
        game2.addPlatform(platform);
        gameRepository.save(game2);

        // Act
        long result = gameRepository.countByPlatformsId(platform.getId());

        // Assert
        Assertions.assertThat(result).isEqualTo(2L);
    }

    @Test
    void findAllWithGameSearchSpecification_withNonMatchingPlatform_returnsEmpty() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("non-matching-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addGenre(genre);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes());

        // Act
        Page<Game> result = gameRepository
                .findAll(gameSearchSpecification, Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findAllWithGameSearchSpecification_withNonMatchingGenre_returnsEmpty() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("non-matching-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addPlatform(platform);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes());

        // Act
        Page<Game> result = gameRepository
                .findAll(gameSearchSpecification, Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findAllWithGameSearchSpecification_withNonMatchingGameMode_returnsEmpty() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("matching-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addPlatform(platform);
        game.addGenre(genre);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), Collections.singleton(GameMode.MULTI_PLAYER));

        // Act
        Page<Game> result = gameRepository
                .findAll(gameSearchSpecification, Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findAllWithGameSearchSpecification_withMatchingCriteria_returnsResults() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("matching-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addPlatform(platform);
        game.addGenre(genre);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes());

        // Act
        Page<Game> result = gameRepository
                .findAll(gameSearchSpecification, Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    void countWithGameSearchSpecification_withNonMatchingPlatform_returnsEmpty() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("non-matching-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addGenre(genre);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes());

        // Act
        long result = gameRepository.count(gameSearchSpecification);

        // Assert
        Assertions.assertThat(result).isZero();
    }

    @Test
    void countWithGameSearchSpecification_withNonMatchingGenre_returnsEmpty() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("non-matching-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addPlatform(platform);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes());

        // Act
        long result = gameRepository.count(gameSearchSpecification);

        // Assert
        Assertions.assertThat(result).isZero();
    }

    @Test
    void countWithGameSearchSpecification_withNonMatchingGameMode_returnsEmpty() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("matching-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addPlatform(platform);
        game.addGenre(genre);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), Collections.singleton(GameMode.MULTI_PLAYER));

        // Act
        long result = gameRepository.count(gameSearchSpecification);

        // Assert
        Assertions.assertThat(result).isZero();
    }

    @Test
    void countWithGameSearchSpecification_withMatchingCriteria_returnsResults() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("matching-platform");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game.addPlatform(platform);
        game.addGenre(genre);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes());

        // Act
        long result = gameRepository.count(gameSearchSpecification);

        // Assert
        Assertions.assertThat(result).isNotZero();
    }

    @Test
    void findBySlug_withNonExistentGame_returnsEmptyOptional() {
        // Act
        Optional<Game> result = gameRepository.findBySlug("test-slug");

        // Assert
        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    void findBySlug_withGame_returnsGame() {
        // Arrange
        Game game = new Game();
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.setSlug("test-slug");
        game = gameRepository.save(game);

        // Act
        Optional<Game> result = gameRepository.findBySlug("test-slug");

        // Assert
        Assertions.assertThat(result).isPresent()
                .isEqualTo(Optional.of(game));
    }
}
