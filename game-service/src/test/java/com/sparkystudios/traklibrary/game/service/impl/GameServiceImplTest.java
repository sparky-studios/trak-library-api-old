package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.repository.*;
import com.sparkystudios.traklibrary.game.repository.specification.GameSpecification;
import com.sparkystudios.traklibrary.game.service.PatchService;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import com.sparkystudios.traklibrary.game.service.mapper.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.json.JsonMergePatch;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private PatchService patchService;

    @Mock
    private GameMapper gameMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private GameServiceImpl gameService;

    @Test
    void save_withNullGameDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameService.save(null));
    }

    @Test
    void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        GameDto gameDto = new GameDto();

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> gameService.save(gameDto));
    }

    @Test
    void save_withNewGameDto_savesGameDto() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(gameRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Game());

        GameDto gameDto = new GameDto();
        gameDto.getReleaseDates().add(new GameReleaseDateDto());
        gameDto.getReleaseDates().add(new GameReleaseDateDto());

        Mockito.when(gameMapper.toGame(ArgumentMatchers.any()))
                .thenReturn(new Game());

        // Act
        gameService.save(gameDto);

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMostOnce())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.findById(0L));
    }

    @Test
    void findById_withValidGame_returnsGameDto() {
        // Arrange
        Game game = new Game();
        game.setId(1L);
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.setVersion(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
            .thenReturn(Optional.of(game));

        Mockito.when(gameMapper.fromGame(ArgumentMatchers.any()))
                .thenReturn(new GameDto());

        // Act
        GameDto result = gameService.findById(0L);

        // Assert
        Assertions.assertNotNull(result, "The mapped result should not be null.");

        Mockito.verify(gameMapper, Mockito.atMostOnce())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findBySlug_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.findBySlug("slug"));
    }

    @Test
    void findBySlug_withValidGame_returnsGameDto() {
        // Arrange
        Game game = new Game();
        game.setId(1L);
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.setVersion(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(game));

        Mockito.when(gameMapper.fromGame(ArgumentMatchers.any()))
                .thenReturn(new GameDto());

        // Act
        GameDto result = gameService.findBySlug("slug");

        // Assert
        Assertions.assertNotNull(result, "The mapped result should not be null.");

        Mockito.verify(gameMapper, Mockito.atMostOnce())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByGenreId_withNonExistentGenre_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Pageable pageable = Mockito.mock(Pageable.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.findGamesByGenreId(0L, pageable));
    }

    @Test
    void findGamesByGenreId_withNoGames_returnsEmptyList() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.findByGenresId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByGenreId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(gameMapper, Mockito.never())
                .fromGame(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.never())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByGenreId_withMultipleGames_returnsList() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.findByGenresId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(new Game(), new Game())));

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByGenreId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void countGamesByGenreId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.countGamesByGenreId(0L));
    }

    @Test
    void countGamesByGenreId_withGenre_invokesGameGenreXrefRepository() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.countByGenresId(ArgumentMatchers.anyLong()))
                .thenReturn(0L);

        // Act
        gameService.countGamesByGenreId(0L);

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .countByGenresId(ArgumentMatchers.anyLong());
    }

    @Test
    void saveGenresForGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Collection<Long> ids = Collections.emptyList();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.saveGenresForGameId(0L, ids));
    }

    @Test
    void saveGenresForGameId_withGame_savesGame() {
        // Arrange
        Game game = Mockito.spy(Game.class);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        Mockito.when(genreRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(List.of(new Genre(), new Genre()));

        Mockito.when(gameRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Game());

        // Act
        gameService.saveGenresForGameId(0L, List.of(0L, 1L));

        // Assert
        Mockito.verify(game, Mockito.times(2))
                .addGenre(ArgumentMatchers.any());

        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMostOnce())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void updateGenresForGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Collection<Long> ids = Collections.emptyList();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.updateGenresForGameId(0L, ids));
    }

    @Test
    void updateGenresForGameId_withGame_updatesGame() {
        // Arrange
        Game game = Mockito.spy(Game.class);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        Mockito.when(genreRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(List.of(new Genre(), new Genre()));

        Mockito.when(gameRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Game());

        // Act
        gameService.updateGenresForGameId(0L, List.of(0L, 1L));

        // Assert
        Mockito.verify(game, Mockito.times(2))
                .addGenre(ArgumentMatchers.any());

        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMostOnce())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByPlatformId_withNonExistentPlatform_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Pageable pageable = Mockito.mock(Pageable.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.findGamesByPlatformId(0L, pageable));
    }

    @Test
    void findGamesByPlatformId_withNoPlatforms_returnsEmptyList() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.findByPlatformsId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByPlatformId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(gameMapper, Mockito.never())
                .fromGame(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.never())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByPlatformId_withMultipleGames_returnsList() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.findByPlatformsId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(new Game(), new Game())));

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByPlatformId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void countGamesByPlatformId_withNonExistentPlatform_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.countGamesByPlatformId(0L));
    }

    @Test
    void countGamesByPlatformId_withPlatform_invokesGamePlatformXrefRepository() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.countByPlatformsId(ArgumentMatchers.anyLong()))
                .thenReturn(0L);

        // Act
        gameService.countGamesByPlatformId(0L);

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .countByPlatformsId(ArgumentMatchers.anyLong());
    }

    @Test
    void savePlatformsForGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Collection<Long> ids = Collections.emptyList();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.savePlatformsForGameId(0L, ids));
    }

    @Test
    void savePlatformsForGameId_withGame_savesGame() {
        // Arrange
        Game game = Mockito.spy(Game.class);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        Mockito.when(platformRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(List.of(new Platform(), new Platform()));

        Mockito.when(gameRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Game());

        // Act
        gameService.savePlatformsForGameId(0L, List.of(0L, 1L));

        // Assert
        Mockito.verify(game, Mockito.times(2))
                .addPlatform(ArgumentMatchers.any());

        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMostOnce())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void updatePlatformsForGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Collection<Long> ids = Collections.emptyList();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.updatePlatformsForGameId(0L, ids));
    }

    @Test
    void updatePlatformsForGameId_withGame_updatesGame() {
        // Arrange
        Game game = Mockito.spy(Game.class);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        Mockito.when(platformRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(List.of(new Platform(), new Platform()));

        Mockito.when(gameRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Game());

        // Act
        gameService.updatePlatformsForGameId(0L, List.of(0L, 1L));

        // Assert
        Mockito.verify(game, Mockito.times(2))
                .addPlatform(ArgumentMatchers.any());

        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMostOnce())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByDeveloperId_withNonExistentDeveloper_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Pageable pageable = Mockito.mock(Pageable.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.findGamesByDeveloperId(0L, pageable));
    }

    @Test
    void findGamesByDeveloperId_withNoDevelopers_returnsEmptyList() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.findByDevelopersId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByDeveloperId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(gameMapper, Mockito.never())
                .fromGame(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.never())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByDeveloperId_withMultipleGames_returnsList() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.findByDevelopersId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(new Game(), new Game())));

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByDeveloperId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void countGamesByDeveloperId_withNonExistentDeveloper_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.countGamesByDeveloperId(0L));
    }

    @Test
    void countGamesByDeveloperId_withDeveloper_invokesGameDeveloperXrefRepository() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.countByDevelopersId(ArgumentMatchers.anyLong()))
                .thenReturn(0L);

        // Act
        gameService.countGamesByDeveloperId(0L);

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .countByDevelopersId(ArgumentMatchers.anyLong());
    }

    @Test
    void saveDevelopersForGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Collection<Long> ids = Collections.emptyList();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.saveDevelopersForGameId(0L, ids));
    }

    @Test
    void saveDevelopersForGameId_withGame_savesGame() {
        // Arrange
        Game game = Mockito.spy(Game.class);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        Mockito.when(developerRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(List.of(new Developer(), new Developer()));

        Mockito.when(gameRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Game());

        // Act
        gameService.saveDevelopersForGameId(0L, List.of(0L, 1L));

        // Assert
        Mockito.verify(game, Mockito.times(2))
                .addDeveloper(ArgumentMatchers.any());

        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMostOnce())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void updateDevelopersForGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Collection<Long> ids = Collections.emptyList();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.updateDevelopersForGameId(0L, ids));
    }

    @Test
    void updateDevelopersForGameId_withGame_updatesGame() {
        // Arrange
        Game game = Mockito.spy(Game.class);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        Mockito.when(developerRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(List.of(new Developer(), new Developer()));

        Mockito.when(gameRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Game());

        // Act
        gameService.updateDevelopersForGameId(0L, List.of(0L, 1L));

        // Assert
        Mockito.verify(game, Mockito.times(2))
                .addDeveloper(ArgumentMatchers.any());

        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMostOnce())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByPublisherId_withNonExistentPublisher_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Pageable pageable = Mockito.mock(Pageable.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.findGamesByPublisherId(0L, pageable));
    }

    @Test
    void findGamesByPublisherId_withNoPublishers_returnsEmptyList() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.findByPublishersId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByPublisherId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(gameMapper, Mockito.never())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByPublisherId_withMultipleGames_returnsList() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.findByPublishersId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(new Game(), new Game())));

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByPublisherId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void countGamesByPublisherId_withNonExistentPublisher_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.countGamesByPublisherId(0L));
    }

    @Test
    void countGamesByPublisherId_withPublisher_invokesGamePublisherXrefRepository() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.countByPublishersId(ArgumentMatchers.anyLong()))
                .thenReturn(0L);

        // Act
        gameService.countGamesByPublisherId(0L);

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .count(ArgumentMatchers.any());
    }

    @Test
    void savePublishersForGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Collection<Long> ids = Collections.emptyList();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.savePublishersForGameId(0L, ids));
    }

    @Test
    void savePublishersForGameId_withGame_savesGame() {
        // Arrange
        Game game = Mockito.spy(Game.class);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        Mockito.when(publisherRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(List.of(new Publisher(), new Publisher()));

        Mockito.when(gameRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Game());

        // Act
        gameService.savePublishersForGameId(0L, List.of(0L, 1L));

        // Assert
        Mockito.verify(game, Mockito.times(2))
                .addPublisher(ArgumentMatchers.any());

        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMostOnce())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void updatePublishersForGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Collection<Long> ids = Collections.emptyList();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.updatePublishersForGameId(0L, ids));
    }

    @Test
    void updatePublishersForGameId_withGame_updatesGame() {
        // Arrange
        Game game = Mockito.spy(Game.class);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        Mockito.when(publisherRepository.findAllById(ArgumentMatchers.anyIterable()))
                .thenReturn(List.of(new Publisher(), new Publisher()));

        Mockito.when(gameRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Game());

        // Act
        gameService.updatePublishersForGameId(0L, List.of(0L, 1L));

        // Assert
        Mockito.verify(game, Mockito.times(2))
                .addPublisher(ArgumentMatchers.any());

        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMostOnce())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByFranchiseId_withNonExistentFranchise_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(franchiseRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Pageable pageable = Mockito.mock(Pageable.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.findGamesByFranchiseId(0L, pageable));
    }

    @Test
    void findGamesByFranchiseId_withNoGames_returnsEmptyList() {
        // Arrange
        Mockito.when(franchiseRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.findByFranchiseId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByFranchiseId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(gameMapper, Mockito.never())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findGamesByFranchiseId_withMultipleGames_returnsList() {
        // Arrange
        Mockito.when(franchiseRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.findByFranchiseId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(new Game(), new Game())));

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByFranchiseId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void countGamesByFranchiseId_withNonExistentFranchise_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(franchiseRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.countGamesByFranchiseId(0L));
    }

    @Test
    void countGamesByFranchiseId_withFranchise_invokesGameRepository() {
        // Arrange
        Mockito.when(franchiseRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.countByFranchiseId(ArgumentMatchers.anyLong()))
                .thenReturn(0L);

        // Act
        gameService.countGamesByFranchiseId(0L);

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .countByFranchiseId(ArgumentMatchers.anyLong());
    }

    @Test
    void findAll_withNoGamesAndNoPageable_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.findAll())
                .thenReturn(Collections.emptyList());

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findAll().spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "There should be no game dto's if no games were found.");

        Mockito.verify(gameMapper, Mockito.never())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findAll_withGamesAndNoPageable_returnsListOfGameDtos() {
        // Arrange
        Mockito.when(gameRepository.findAll())
                .thenReturn(Arrays.asList(new Game(), new Game()));

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findAll().spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "There should be game dto's if games were found.");

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findAll_withNullPageable_throwsNullPointerException() {
        // Arrange
        GameSpecification gameSpecification = Mockito.mock(GameSpecification.class);

        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameService.findAll(gameSpecification, null));
    }

    @Test
    void findAll_withNoGames_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.findAll(ArgumentMatchers.any(GameSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        GameSpecification gameSpecification = Mockito.mock(GameSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findAll(gameSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no pages game results were found.");

        Mockito.verify(gameMapper, Mockito.never())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void findAll_withGames_returnsGamesAsGameDtos() {
        // Arrange
        Page<Game> games = new PageImpl<>(Arrays.asList(new Game(), new Game()));

        Mockito.when(gameRepository.findAll(ArgumentMatchers.any(GameSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(games);

        GameSpecification gameSpecification = Mockito.mock(GameSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findAll(gameSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result shouldn't be empty if the repository returned games.");

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void count_withNullGameSpecification_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameService.count(null));
    }

    @Test
    void count_withValidGameSpecification_invokesCount() {
        // Arrange
        Mockito.when(gameRepository.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        gameService.count(Mockito.mock(GameSpecification.class));

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .count(Mockito.any());
    }

    @Test
    void update_withNullGameDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameService.update(null));
    }

    @Test
    void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        GameDto gameDto = new GameDto();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.update(gameDto));
    }

    @Test
    void update_withExistingGameDto_updatesGameDto() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Game());

        GameDto gameDto = new GameDto();
        gameDto.getReleaseDates().add(new GameReleaseDateDto());
        gameDto.getReleaseDates().add(new GameReleaseDateDto());

        Mockito.when(gameMapper.toGame(ArgumentMatchers.any()))
                .thenReturn(new Game());

        // Act
        gameService.update(gameDto);

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMostOnce())
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.deleteById(0L));
    }

    @Test
    void patch_withNoGameMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        JsonMergePatch jsonMergePatch = Mockito.mock(JsonMergePatch.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.patch(0L, jsonMergePatch));
    }

    @Test
    void patch_withValidId_saveGame() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Game()));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(patchService.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new GameDto());

        // Act
        gameService.patch(0L, Mockito.mock(JsonMergePatch.class));

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .fromGame(ArgumentMatchers.any());
    }

    @Test
    void delete_withExistingId_invokesDeletion() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.doNothing().when(gameRepository)
                .deleteById(ArgumentMatchers.anyLong());

        // Act
        gameService.deleteById(0L);

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .deleteById(ArgumentMatchers.anyLong());
    }
}
