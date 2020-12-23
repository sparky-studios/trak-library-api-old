package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.repository.specification.GameSearchSpecification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;

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
        developer = developerRepository.save(developer);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1.addDeveloper(developer);
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
        game2.addDeveloper(developer);
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
        Assertions.assertThat(result).isEqualTo(0L);
    }

    @Test
    void countByDevelopersId_withGames_returnsGameCount() {
        // Arrange
        Developer developer = new Developer();
        developer.setName("test-developer");
        developer.setDescription("test-description");
        developer.setFoundedDate(LocalDate.now());
        developer = developerRepository.save(developer);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1.addDeveloper(developer);
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
        game2.addDeveloper(developer);
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
        publisher = publisherRepository.save(publisher);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1.addPublisher(publisher);
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
        game2.addPublisher(publisher);
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
        Assertions.assertThat(result).isEqualTo(0L);
    }

    @Test
    void countByPublishersId_withGames_returnsGameCount() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setName("test-developer");
        publisher.setDescription("test-description");
        publisher.setFoundedDate(LocalDate.now());
        publisher = publisherRepository.save(publisher);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1.addPublisher(publisher);
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
        game2.addPublisher(publisher);
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
        genre = genreRepository.save(genre);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1.addGenre(genre);
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
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
        Assertions.assertThat(result).isEqualTo(0L);
    }

    @Test
    void countByGenresId_withGames_returnsGameCount() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("test-genre");
        genre.setDescription("test-description");
        genre = genreRepository.save(genre);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1.addGenre(genre);
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
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
        platform = platformRepository.save(platform);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1.addPlatform(platform);
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
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
        Assertions.assertThat(result).isEqualTo(0L);
    }

    @Test
    void countByPlatformsId_withGames_returnsGameCount() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("test-platform");
        platform.setDescription("test-description");
        platform = platformRepository.save(platform);

        Game game1 = new Game();
        game1.setTitle("game-title-1");
        game1.setDescription("game-description-1");
        game1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game1.addPlatform(platform);
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setTitle("game-title-2");
        game2.setDescription("game-description-2");
        game2.setAgeRating(AgeRating.ADULTS_ONLY);
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
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.addGenre(genre);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes(), Collections.singleton(game.getAgeRating()));

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
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.addPlatform(platform);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes(), Collections.singleton(game.getAgeRating()));

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
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.addPlatform(platform);
        game.addGenre(genre);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), Collections.singleton(GameMode.MULTI_PLAYER), Collections.singleton(game.getAgeRating()));

        // Act
        Page<Game> result = gameRepository
                .findAll(gameSearchSpecification, Pageable.unpaged());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findAllWithGameSearchSpecification_withNonMatchingAgeRating_returnsEmpty() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("matching-platform");
        platform.setDescription("test-description");
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.addPlatform(platform);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes(), Collections.singleton(AgeRating.ADULTS_ONLY));

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
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.addPlatform(platform);
        game.addGenre(genre);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes(), Collections.singleton(game.getAgeRating()));

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
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.addGenre(genre);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes(), Collections.singleton(game.getAgeRating()));

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
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.addPlatform(platform);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes(), Collections.singleton(game.getAgeRating()));

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
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.addPlatform(platform);
        game.addGenre(genre);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), Collections.singleton(GameMode.MULTI_PLAYER), Collections.singleton(game.getAgeRating()));

        // Act
        long result = gameRepository.count(gameSearchSpecification);

        // Assert
        Assertions.assertThat(result).isZero();
    }

    @Test
    void countWithGameSearchSpecification_withNonMatchingAgeRating_returnsEmpty() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("matching-platform");
        platform.setDescription("test-description");
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.addPlatform(platform);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes(), Collections.singleton(AgeRating.ADULTS_ONLY));

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
        platform = platformRepository.save(platform);

        Genre genre = new Genre();
        genre.setName("matching-genre");
        genre.setDescription("test-description");
        genre = genreRepository.save(genre);

        Game game = new Game();
        game.setTitle("game-title-1");
        game.setDescription("game-description-1");
        game.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        game.setGameModes(Collections.singleton(GameMode.SINGLE_PLAYER));
        game.addPlatform(platform);
        game.addGenre(genre);
        gameRepository.save(game);

        GameSearchSpecification gameSearchSpecification =
                new GameSearchSpecification(Collections.singleton(platform), Collections.singleton(genre), game.getGameModes(), Collections.singleton(game.getAgeRating()));

        // Act
        long result = gameRepository.count(gameSearchSpecification);

        // Assert
        Assertions.assertThat(result).isNotZero();
    }
}
