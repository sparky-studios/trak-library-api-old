package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.repository.GameUserEntryRepository;
import com.sparkystudios.traklibrary.game.repository.GenreRepository;
import com.sparkystudios.traklibrary.game.repository.PlatformRepository;
import com.sparkystudios.traklibrary.game.repository.specification.GameSearchSpecification;
import com.sparkystudios.traklibrary.game.repository.specification.GameUserEntrySearchSpecification;
import com.sparkystudios.traklibrary.game.service.dto.GameDetailsDto;
import com.sparkystudios.traklibrary.game.service.dto.GameFiltersDto;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryFiltersDto;
import com.sparkystudios.traklibrary.game.service.mapper.GameDetailsMapper;
import com.sparkystudios.traklibrary.game.service.mapper.GameUserEntryMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class GameFilterServiceImplTest {

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameUserEntryRepository gameUserEntryRepository;

    @Mock
    private GameDetailsMapper gameDetailsMapper;

    @Mock
    private GameUserEntryMapper gameUserEntryMapper;

    @InjectMocks
    private GameFilterServiceImpl gameFilterService;

    @Test
    void getGameFilters_withData_returnsPopulatedGameFiltersDto() {
        // Arrange
        Platform platform = new Platform();
        platform.setId(1L);
        platform.setName("test-platform");

        Mockito.when(platformRepository.findAll())
                .thenReturn(Collections.singletonList(platform));

        Genre genre = new Genre();
        genre.setId(2L);
        genre.setName("test-genre");

        Mockito.when(genreRepository.findAll())
                .thenReturn(Collections.singletonList(genre));

        // Act
        GameFiltersDto result = gameFilterService.getGameFilters();

        // Assert
        Assertions.assertThat(result.getPlatforms()).hasSize(1);
        Assertions.assertThat(result.getPlatforms().iterator().next().getId())
                .isEqualTo(platform.getId());
        Assertions.assertThat(result.getPlatforms().iterator().next().getName())
                .isEqualTo(platform.getName());

        Assertions.assertThat(result.getGenres()).hasSize(1);
        Assertions.assertThat(result.getGenres().iterator().next().getId())
                .isEqualTo(genre.getId());
        Assertions.assertThat(result.getGenres().iterator().next().getName())
                .isEqualTo(genre.getName());

        Assertions.assertThat(result.getGameModes()).hasSize(GameMode.values().length);
        Assertions.assertThat(result.getAgeRatings()).hasSize(AgeRating.values().length);
    }

    @Test
    void getGameUserEntryFilters_withData_returnsPopulatedGameUserEntryFiltersDto() {
        // Arrange
        Platform platform = new Platform();
        platform.setId(1L);
        platform.setName("test-platform");

        Mockito.when(platformRepository.findAll())
                .thenReturn(Collections.singletonList(platform));

        Genre genre = new Genre();
        genre.setId(2L);
        genre.setName("test-genre");

        Mockito.when(genreRepository.findAll())
                .thenReturn(Collections.singletonList(genre));

        // Act
        GameUserEntryFiltersDto result = gameFilterService.getGameUserEntryFilters();

        // Assert
        Assertions.assertThat(result.getPlatforms()).hasSize(1);
        Assertions.assertThat(result.getPlatforms().iterator().next().getId())
                .isEqualTo(platform.getId());
        Assertions.assertThat(result.getPlatforms().iterator().next().getName())
                .isEqualTo(platform.getName());

        Assertions.assertThat(result.getGenres()).hasSize(1);
        Assertions.assertThat(result.getGenres().iterator().next().getId())
                .isEqualTo(genre.getId());
        Assertions.assertThat(result.getGenres().iterator().next().getName())
                .isEqualTo(genre.getName());

        Assertions.assertThat(result.getGameModes()).hasSize(GameMode.values().length);
        Assertions.assertThat(result.getAgeRatings()).hasSize(AgeRating.values().length);
        Assertions.assertThat(result.getStatuses()).hasSize(GameUserEntryStatus.values().length);
    }

    @Test
    void findGamesByFilters_withNullPlatformIds_doesntInvokePlatformRepository() {
        // Arrange
        Set<Long> genreIds = Collections.singleton(1L);
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();

        Mockito.when(genreRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Genre()));

        Mockito.when(gameRepository.findAll(ArgumentMatchers.any(GameSearchSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Game(), new Game())));

        Mockito.when(gameDetailsMapper.fromGame(ArgumentMatchers.any()))
                .thenReturn(new GameDetailsDto());

        // Act
        gameFilterService.findGamesByFilters(null, genreIds, gameModes, ageRatings, Pageable.unpaged());

        // Assert
        Mockito.verify(platformRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(gameDetailsMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByFilters_withNoPlatformIds_doesntInvokePlatformRepository() {
        // Arrange
        Set<Long> platformIds = Collections.emptySet();
        Set<Long> genreIds = Collections.singleton(1L);
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();

        Mockito.when(genreRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Genre()));

        Mockito.when(gameRepository.findAll(ArgumentMatchers.any(GameSearchSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Game(), new Game())));

        Mockito.when(gameDetailsMapper.fromGame(ArgumentMatchers.any()))
                .thenReturn(new GameDetailsDto());

        // Act
        gameFilterService.findGamesByFilters(platformIds, genreIds, gameModes, ageRatings, Pageable.unpaged());

        // Assert
        Mockito.verify(platformRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(gameDetailsMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByFilters_withNullGenreIds_doesntInvokeGenreRepository() {
        // Arrange
        Set<Long> platformIds = Collections.singleton(1L);
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();

        Mockito.when(platformRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Platform()));

        Mockito.when(gameRepository.findAll(ArgumentMatchers.any(GameSearchSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Game(), new Game())));

        Mockito.when(gameDetailsMapper.fromGame(ArgumentMatchers.any()))
                .thenReturn(new GameDetailsDto());

        // Act
        gameFilterService.findGamesByFilters(platformIds, null, gameModes, ageRatings, Pageable.unpaged());

        // Assert
        Mockito.verify(genreRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(gameDetailsMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByFilters_withNoGenreIds_doesntInvokeGenreRepository() {
        // Arrange
        Set<Long> platformIds = Collections.singleton(1L);
        Set<Long> genreIds = Collections.emptySet();
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();

        Mockito.when(platformRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Platform()));

        Mockito.when(gameRepository.findAll(ArgumentMatchers.any(GameSearchSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Game(), new Game())));

        Mockito.when(gameDetailsMapper.fromGame(ArgumentMatchers.any()))
                .thenReturn(new GameDetailsDto());

        // Act
        gameFilterService.findGamesByFilters(platformIds, genreIds, gameModes, ageRatings, Pageable.unpaged());

        // Assert
        Mockito.verify(genreRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(gameDetailsMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByFilters_withPlatformAndGenreIds_invokesRepositories() {
        // Arrange
        Set<Long> platformIds = Collections.singleton(1L);
        Set<Long> genreIds = Collections.emptySet();
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();

        Mockito.when(gameRepository.findAll(ArgumentMatchers.any(GameSearchSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Game(), new Game())));

        Mockito.when(gameDetailsMapper.fromGame(ArgumentMatchers.any()))
                .thenReturn(new GameDetailsDto());

        // Act
        gameFilterService.findGamesByFilters(platformIds, genreIds, gameModes, ageRatings, Pageable.unpaged());

        // Assert
        Mockito.verify(platformRepository, Mockito.atMostOnce())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(genreRepository, Mockito.atMostOnce())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(gameDetailsMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void countGamesByFilters_withNullPlatformIds_doesntInvokePlatformRepository() {
        // Arrange
        Set<Long> genreIds = Collections.singleton(1L);
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();

        Mockito.when(genreRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Genre()));

        Mockito.when(gameRepository.count(ArgumentMatchers.any(GameSearchSpecification.class)))
                .thenReturn(0L);

        // Act
        gameFilterService.countGamesByFilters(null, genreIds, gameModes, ageRatings);

        // Assert
        Mockito.verify(platformRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());
    }

    @Test
    void countGamesByFilters_withNoPlatformIds_doesntInvokePlatformRepository() {
        // Arrange
        Set<Long> platformIds = Collections.emptySet();
        Set<Long> genreIds = Collections.singleton(1L);
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();

        Mockito.when(genreRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Genre()));

        Mockito.when(gameRepository.count(ArgumentMatchers.any(GameSearchSpecification.class)))
                .thenReturn(0L);

        // Act
        gameFilterService.countGamesByFilters(platformIds, genreIds, gameModes, ageRatings);

        // Assert
        Mockito.verify(platformRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());
    }

    @Test
    void countGamesByFilters_withNullGenreIds_doesntInvokeGenreRepository() {
        // Arrange
        Set<Long> platformIds = Collections.singleton(1L);
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();

        Mockito.when(platformRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Platform()));

        Mockito.when(gameRepository.count(ArgumentMatchers.any(GameSearchSpecification.class)))
                .thenReturn(0L);

        // Act
        gameFilterService.countGamesByFilters(platformIds, null, gameModes, ageRatings);

        // Assert
        Mockito.verify(genreRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());
    }

    @Test
    void countGamesByFilters_withNoGenreIds_doesntInvokeGenreRepository() {
        // Arrange
        Set<Long> platformIds = Collections.singleton(1L);
        Set<Long> genreIds = Collections.emptySet();
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();

        Mockito.when(platformRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Platform()));

        Mockito.when(gameRepository.count(ArgumentMatchers.any(GameSearchSpecification.class)))
                .thenReturn(0L);

        // Act
        gameFilterService.countGamesByFilters(platformIds, genreIds, gameModes, ageRatings);

        // Assert
        Mockito.verify(genreRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());
    }

    @Test
    void countGamesByFilters_withPlatformAndGenreIds_invokesRepositories() {
        // Arrange
        Set<Long> platformIds = Collections.singleton(1L);
        Set<Long> genreIds = Collections.emptySet();
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();

        Mockito.when(gameRepository.count(ArgumentMatchers.any(GameSearchSpecification.class)))
                .thenReturn(0L);

        // Act
        gameFilterService.countGamesByFilters(platformIds, genreIds, gameModes, ageRatings);

        // Assert
        Mockito.verify(platformRepository, Mockito.atMostOnce())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(genreRepository, Mockito.atMostOnce())
                .findAllById(ArgumentMatchers.anyIterable());
    }

    @Test
    void findGameUserEntriesByFilters_withNullPlatformIds_doesntInvokePlatformRepository() {
        // Arrange
        Set<Long> genreIds = Collections.singleton(1L);
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();
        Set<GameUserEntryStatus> statuses = Collections.emptySet();

        Mockito.when(genreRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Genre()));

        Mockito.when(gameUserEntryRepository.findAll(ArgumentMatchers.any(GameUserEntrySearchSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new GameUserEntry(), new GameUserEntry())));

        Mockito.when(gameUserEntryMapper.fromGameUserEntry(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntryDto());

        // Act
        gameFilterService.findGameUserEntriesByFilters(null, genreIds, gameModes, ageRatings, statuses, Pageable.unpaged());

        // Assert
        Mockito.verify(platformRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(gameUserEntryMapper, Mockito.atMost(2))
                .fromGameUserEntry(ArgumentMatchers.any());
    }

    @Test
    void findGameUserEntriesByFilters_withNoPlatformIds_doesntInvokePlatformRepository() {
        // Arrange
        Set<Long> platformIds = Collections.emptySet();
        Set<Long> genreIds = Collections.singleton(1L);
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();
        Set<GameUserEntryStatus> statuses = Collections.emptySet();

        Mockito.when(genreRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Genre()));

        Mockito.when(gameUserEntryRepository.findAll(ArgumentMatchers.any(GameUserEntrySearchSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new GameUserEntry(), new GameUserEntry())));

        Mockito.when(gameUserEntryMapper.fromGameUserEntry(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntryDto());
        // Act
        gameFilterService.findGameUserEntriesByFilters(platformIds, genreIds, gameModes, ageRatings, statuses, Pageable.unpaged());

        // Assert
        Mockito.verify(platformRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(gameUserEntryMapper, Mockito.atMost(2))
                .fromGameUserEntry(ArgumentMatchers.any());
    }

    @Test
    void findGameUserEntriesByFilters_withNullGenreIds_doesntInvokeGenreRepository() {
        // Arrange
        Set<Long> platformIds = Collections.singleton(1L);
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();
        Set<GameUserEntryStatus> statuses = Collections.emptySet();

        Mockito.when(platformRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Platform()));

        Mockito.when(gameUserEntryRepository.findAll(ArgumentMatchers.any(GameUserEntrySearchSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new GameUserEntry(), new GameUserEntry())));

        Mockito.when(gameUserEntryMapper.fromGameUserEntry(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntryDto());

        // Act
        gameFilterService.findGameUserEntriesByFilters(platformIds, null, gameModes, ageRatings, statuses, Pageable.unpaged());

        // Assert
        Mockito.verify(genreRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(gameUserEntryMapper, Mockito.atMost(2))
                .fromGameUserEntry(ArgumentMatchers.any());
    }

    @Test
    void findGameUserEntriesByFilters_withNoGenreIds_doesntInvokeGenreRepository() {
        // Arrange
        Set<Long> platformIds = Collections.singleton(1L);
        Set<Long> genreIds = Collections.emptySet();
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();
        Set<GameUserEntryStatus> statuses = Collections.emptySet();

        Mockito.when(platformRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Platform()));

        Mockito.when(gameUserEntryRepository.findAll(ArgumentMatchers.any(GameUserEntrySearchSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new GameUserEntry(), new GameUserEntry())));

        Mockito.when(gameUserEntryMapper.fromGameUserEntry(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntryDto());

        // Act
        gameFilterService.findGameUserEntriesByFilters(platformIds, genreIds, gameModes, ageRatings, statuses, Pageable.unpaged());

        // Assert
        Mockito.verify(genreRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(gameUserEntryMapper, Mockito.atMost(2))
                .fromGameUserEntry(ArgumentMatchers.any());
    }

    @Test
    void findGameUserEntriesByFilters_withPlatformAndGenreIds_invokesRepositories() {
        // Arrange
        Set<Long> platformIds = Collections.singleton(1L);
        Set<Long> genreIds = Collections.emptySet();
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();
        Set<GameUserEntryStatus> statuses = Collections.emptySet();

        Mockito.when(gameUserEntryRepository.findAll(ArgumentMatchers.any(GameUserEntrySearchSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new GameUserEntry(), new GameUserEntry())));

        Mockito.when(gameUserEntryMapper.fromGameUserEntry(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntryDto());

        // Act
        gameFilterService.findGameUserEntriesByFilters(platformIds, genreIds, gameModes, ageRatings, statuses, Pageable.unpaged());

        // Assert
        Mockito.verify(platformRepository, Mockito.atMostOnce())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(genreRepository, Mockito.atMostOnce())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(gameUserEntryMapper, Mockito.atMost(2))
                .fromGameUserEntry(ArgumentMatchers.any());
    }

    @Test
    void countGameUserEntriesByFilters_withNullPlatformIds_doesntInvokePlatformRepository() {
        // Arrange
        Set<Long> genreIds = Collections.singleton(1L);
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();
        Set<GameUserEntryStatus> statuses = Collections.emptySet();

        Mockito.when(genreRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Genre()));

        Mockito.when(gameUserEntryRepository.count(ArgumentMatchers.any(GameUserEntrySearchSpecification.class)))
                .thenReturn(0L);

        // Act
        gameFilterService.countGameUserEntriesByFilters(null, genreIds, gameModes, ageRatings, statuses);

        // Assert
        Mockito.verify(platformRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());
    }

    @Test
    void countGameUserEntriesByFilters_withNoPlatformIds_doesntInvokePlatformRepository() {
        // Arrange
        Set<Long> platformIds = Collections.emptySet();
        Set<Long> genreIds = Collections.singleton(1L);
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();
        Set<GameUserEntryStatus> statuses = Collections.emptySet();

        Mockito.when(genreRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Genre()));

        Mockito.when(gameUserEntryRepository.count(ArgumentMatchers.any(GameUserEntrySearchSpecification.class)))
                .thenReturn(0L);

        // Act
        gameFilterService.countGameUserEntriesByFilters(platformIds, genreIds, gameModes, ageRatings, statuses);

        // Assert
        Mockito.verify(platformRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());
    }

    @Test
    void countGameUserEntriesByFilters_withNullGenreIds_doesntInvokeGenreRepository() {
        // Arrange
        Set<Long> platformIds = Collections.singleton(1L);
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();
        Set<GameUserEntryStatus> statuses = Collections.emptySet();

        Mockito.when(platformRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Platform()));

        Mockito.when(gameUserEntryRepository.count(ArgumentMatchers.any(GameUserEntrySearchSpecification.class)))
                .thenReturn(0L);

        // Act
        gameFilterService.countGameUserEntriesByFilters(platformIds, null, gameModes, ageRatings, statuses);

        // Assert
        Mockito.verify(genreRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());
    }

    @Test
    void countGameUserEntriesByFilters_withNoGenreIds_doesntInvokeGenreRepository() {
        // Arrange
        Set<Long> platformIds = Collections.singleton(1L);
        Set<Long> genreIds = Collections.emptySet();
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();
        Set<GameUserEntryStatus> statuses = Collections.emptySet();

        Mockito.when(platformRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(Collections.singletonList(new Platform()));

        Mockito.when(gameUserEntryRepository.count(ArgumentMatchers.any(GameUserEntrySearchSpecification.class)))
                .thenReturn(0L);

        // Act
        gameFilterService.countGameUserEntriesByFilters(platformIds, genreIds, gameModes, ageRatings, statuses);

        // Assert
        Mockito.verify(genreRepository, Mockito.never())
                .findAllById(ArgumentMatchers.anyIterable());
    }

    @Test
    void countGameUserEntriesByFilters_withPlatformAndGenreIds_invokesRepositories() {
        // Arrange
        Set<Long> platformIds = Collections.singleton(1L);
        Set<Long> genreIds = Collections.emptySet();
        Set<GameMode> gameModes = Collections.emptySet();
        Set<AgeRating> ageRatings = Collections.emptySet();
        Set<GameUserEntryStatus> statuses = Collections.emptySet();

        Mockito.when(gameUserEntryRepository.count(ArgumentMatchers.any(GameUserEntrySearchSpecification.class)))
                .thenReturn(0L);

        // Act
        gameFilterService.countGameUserEntriesByFilters(platformIds, genreIds, gameModes, ageRatings, statuses);

        // Assert
        Mockito.verify(platformRepository, Mockito.atMostOnce())
                .findAllById(ArgumentMatchers.anyIterable());

        Mockito.verify(genreRepository, Mockito.atMostOnce())
                .findAllById(ArgumentMatchers.anyIterable());
    }
}
