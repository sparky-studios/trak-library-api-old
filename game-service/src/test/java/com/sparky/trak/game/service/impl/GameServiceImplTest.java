package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.domain.*;
import com.sparky.trak.game.repository.*;
import com.sparky.trak.game.repository.specification.GameSpecification;
import com.sparky.trak.game.service.PatchService;
import com.sparky.trak.game.service.dto.GameDto;
import com.sparky.trak.game.service.mapper.GameMapper;
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
public class GameServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GameGenreXrefRepository gameGenreXrefRepository;

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private GamePlatformXrefRepository gamePlatformXrefRepository;

    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private GameDeveloperXrefRepository gameDeveloperXrefRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private GamePublisherXrefRepository gamePublisherXrefRepository;

    @Mock
    private PatchService patchService;

    @Spy
    private GameMapper gameMapper = GameMapper.INSTANCE;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private GameServiceImpl gameService;

    @Test
    public void save_withNullGameDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameService.save(null));
    }

    @Test
    public void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> gameService.save(new GameDto()));
    }

    @Test
    public void save_withNewGameDto_savesGameDto() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(gameRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Game());

        // Act
        gameService.save(new GameDto());

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.findById(0L));
    }

    @Test
    public void findById_withValidGame_returnsGameDto() {
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

        // Act
        GameDto result = gameService.findById(0L);

        // Assert
        Assertions.assertEquals(game.getId(), result.getId(), "The ID does match the entity.");
        Assertions.assertEquals(game.getTitle(), result.getTitle(), "The title does match the entity.");
        Assertions.assertEquals(game.getDescription(), result.getDescription(), "The description does match the entity.");
        Assertions.assertEquals(game.getReleaseDate(), result.getReleaseDate(), "The release date does match the entity.");
        Assertions.assertEquals(game.getVersion(), result.getVersion(), "The version does match the entity.");
    }

    @Test
    public void findGamesByGenreId_withNonExistentGenre_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.findGamesByGenreId(0L, Mockito.mock(Pageable.class)));
    }

    @Test
    public void findGamesByGenreId_withNoGames_returnsEmptyList() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameGenreXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByGenreId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(gameMapper, Mockito.never())
                .gameToGameDto(ArgumentMatchers.any());
    }

    @Test
    public void findGamesByGenreId_withMultipleGames_returnsList() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        GameGenreXref gameGenreXref1 = new GameGenreXref();
        gameGenreXref1.setGame(new Game());

        GameGenreXref gameGenreXref2 = new GameGenreXref();
        gameGenreXref2.setGame(new Game());

        Mockito.when(gameGenreXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(gameGenreXref1, gameGenreXref2)));

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByGenreId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .gameToGameDto(ArgumentMatchers.any());
    }

    @Test
    public void findGamesByPlatformId_withNonExistentPlatform_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.findGamesByPlatformId(0L, Mockito.mock(Pageable.class)));
    }

    @Test
    public void findGamesByPlatformId_withNoPlatforms_returnsEmptyList() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gamePlatformXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByPlatformId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(gameMapper, Mockito.never())
                .gameToGameDto(ArgumentMatchers.any());
    }

    @Test
    public void findGamesByPlatformId_withMultipleGames_returnsList() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        GamePlatformXref gamePlatformXref1 = new GamePlatformXref();
        gamePlatformXref1.setGame(new Game());

        GamePlatformXref gamePlatformXref2 = new GamePlatformXref();
        gamePlatformXref2.setGame(new Game());

        Mockito.when(gamePlatformXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(gamePlatformXref1, gamePlatformXref2)));

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByPlatformId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .gameToGameDto(ArgumentMatchers.any());
    }

    @Test
    public void findGamesByDeveloperId_withNonExistentDeveloper_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.findGamesByDeveloperId(0L, Mockito.mock(Pageable.class)));
    }

    @Test
    public void findGamesByDeveloperId_withNoDevelopers_returnsEmptyList() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameDeveloperXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByDeveloperId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(gameMapper, Mockito.never())
                .gameToGameDto(ArgumentMatchers.any());
    }

    @Test
    public void findGamesByDeveloperId_withMultipleGames_returnsList() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        GameDeveloperXref gameDeveloperXref1 = new GameDeveloperXref();
        gameDeveloperXref1.setGame(new Game());

        GameDeveloperXref gameDeveloperXref2 = new GameDeveloperXref();
        gameDeveloperXref2.setGame(new Game());

        Mockito.when(gameDeveloperXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(gameDeveloperXref1, gameDeveloperXref2)));

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByDeveloperId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .gameToGameDto(ArgumentMatchers.any());
    }

    @Test
    public void findGamesByPublisherId_withNonExistentPublisher_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.findGamesByPublisherId(0L, Mockito.mock(Pageable.class)));
    }

    @Test
    public void findGamesByPublisherId_withNoPublishers_returnsEmptyList() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gamePublisherXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByPublisherId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(gameMapper, Mockito.never())
                .gameToGameDto(ArgumentMatchers.any());
    }

    @Test
    public void findGamesByPublisherId_withMultipleGames_returnsList() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        GamePublisherXref gamePublisherXref1 = new GamePublisherXref();
        gamePublisherXref1.setGame(new Game());

        GamePublisherXref gamePublisherXref2 = new GamePublisherXref();
        gamePublisherXref1.setGame(new Game());

        Mockito.when(gamePublisherXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(gamePublisherXref1, gamePublisherXref2)));

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findGamesByPublisherId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .gameToGameDto(ArgumentMatchers.any());
    }

    @Test
    public void findAll_withNoGamesAndNoPageable_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.findAll())
                .thenReturn(Collections.emptyList());

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findAll().spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "There should be no game dto's if no games were found.");

        Mockito.verify(gameMapper, Mockito.never())
                .gameToGameDto(ArgumentMatchers.any());
    }

    @Test
    public void findAll_withGamesAndNoPageable_returnsListOfGameDtos() {
        // Arrange
        Mockito.when(gameRepository.findAll())
                .thenReturn(Arrays.asList(new Game(), new Game()));

        // Act
        List<GameDto> result = StreamSupport.stream(gameService.findAll().spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "There should be game dto's if games were found.");

        Mockito.verify(gameMapper, Mockito.atMost(2))
                .gameToGameDto(ArgumentMatchers.any());
    }

    @Test
    public void findAll_withNullPageable_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameService.findAll(Mockito.mock(GameSpecification.class), null));
    }

    @Test
    public void findAll_withNoGames_returnsEmptyList() {
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
    }

    @Test
    public void findAll_withGames_returnsGamesAsGameDtos() {
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
    }

    @Test
    public void update_withNullGameDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameService.update(null));
    }

    @Test
    public void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.update(new GameDto()));
    }

    @Test
    public void update_withExistingGameDto_updatesGameDto() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Game());

        // Act
        gameService.update(new GameDto());

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.deleteById(0L));
    }

    @Test
    public void patch_withNoGameMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameService.patch(0L, Mockito.mock(JsonMergePatch.class)));
    }

    @Test
    public void patch_withValidId_saveGame() {
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
    }

    @Test
    public void delete_withExistingId_invokesDeletion() {
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
