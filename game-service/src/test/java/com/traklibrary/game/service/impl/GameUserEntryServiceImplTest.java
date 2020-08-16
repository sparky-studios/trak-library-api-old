package com.traklibrary.game.service.impl;

import com.traklibrary.game.repository.GameRepository;
import com.traklibrary.game.repository.GameUserEntryRepository;
import com.traklibrary.game.repository.specification.GameUserEntrySpecification;
import com.traklibrary.game.service.AuthenticationService;
import com.traklibrary.game.service.PatchService;
import com.traklibrary.game.service.dto.GameUserEntryDto;
import com.traklibrary.game.service.exception.InvalidUserException;
import com.traklibrary.game.service.mapper.GameMappers;
import com.traklibrary.game.service.mapper.GameUserEntryMapper;
import com.traklibrary.game.domain.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ExtendWith(MockitoExtension.class)
class GameUserEntryServiceImplTest {

    @Mock
    private GameUserEntryRepository gameUserEntryRepository;

    @Mock
    private GameRepository gameRepository;

    @Spy
    private final GameUserEntryMapper gameUserEntryMapper = GameMappers.GAME_USER_ENTRY_MAPPER;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private PatchService patchService;

    @InjectMocks
    private GameUserEntryServiceImpl gameUserEntryService;

    @Test
    void save_withNullGameUserEntryDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameUserEntryService.save(null));
        Mockito.verify(gameUserEntryRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void save_againstDifferentUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(InvalidUserException.class, () -> gameUserEntryService.save(new GameUserEntryDto()));
        Mockito.verify(gameUserEntryRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameUserEntryRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> gameUserEntryService.save(new GameUserEntryDto()));
        Mockito.verify(gameUserEntryRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void save_withNewGameUserEntryDto_savesGameUserEntryDto() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameUserEntryRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(gameUserEntryRepository.save(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntry());

        // Act
        gameUserEntryService.save(new GameUserEntryDto());

        // Assert
        Mockito.verify(gameUserEntryRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameUserEntryService.findById(0L));
    }

    @Test
    void findById_withValidGameUserEntry_returnsGameUserEntryDto() {
        // Arrange
        Game game = new Game();
        game.setTitle("test-title");

        Platform platform = new Platform();
        platform.setName("test-name");

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setId(1L);
        gameUserEntry.setGameId(2L);
        gameUserEntry.setGame(game);
        gameUserEntry.setPlatformId(3L);
        gameUserEntry.setPlatform(platform);
        gameUserEntry.setUserId(4L);
        gameUserEntry.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntry.setRating((short)4);
        gameUserEntry.setVersion(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(gameUserEntry));

        // Act
        GameUserEntryDto result = gameUserEntryService.findById(0L);

        // Assert
        Assertions.assertEquals(gameUserEntry.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getGameId(), result.getGameId(), "The mapped game ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getGame().getTitle(), result.getGameTitle(), "The mapped game title does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getPlatformId(), result.getPlatformId(), "The mapped platform ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getPlatform().getName(), result.getPlatformName(), "The mapped platform name does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getUserId(), result.getUserId(), "The mapped user ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getStatus(), result.getStatus(), "The mapped status does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getRating(), result.getRating(), "The mapped rating does not match the entity.");
    }

    @Test
    void findGameUserEntriesByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameUserEntryService.findGameUserEntriesByGameId(0L, Mockito.mock(Pageable.class)));
    }

    @Test
    void findGameUserEntriesByGameId_withNoGameUserEntries_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameUserEntryRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<GameUserEntryDto> result = StreamSupport.stream(gameUserEntryService.findGameUserEntriesByGameId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no game user entries are returned.");

        Mockito.verify(gameUserEntryMapper, Mockito.never())
                .gameUserEntryToGameUserEntryDto(ArgumentMatchers.any());
    }

    @Test
    void findGamesByGenreId_withMultipleGames_returnsList() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        GameGenreXref gameGenreXref1 = new GameGenreXref();
        gameGenreXref1.setGame(new Game());

        GameGenreXref gameGenreXref2 = new GameGenreXref();
        gameGenreXref2.setGame(new Game());

        Mockito.when(gameUserEntryRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(new GameUserEntry(), new GameUserEntry())));

        // Act
        List<GameUserEntryDto> result = StreamSupport.stream(gameUserEntryService.findGameUserEntriesByGameId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(gameUserEntryMapper, Mockito.atMost(2))
                .gameUserEntryToGameUserEntryDto(ArgumentMatchers.any());
    }

    @Test
    void countGameUserEntriesByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameUserEntryService.countGameUserEntriesByGameId(0L));
    }

    @Test
    void countGameUserEntriesByGameId_withGame_invokesGameUserEntryRepository() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameUserEntryRepository.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        gameUserEntryService.countGameUserEntriesByGameId(0L);

        // Assert
        Mockito.verify(gameUserEntryRepository, Mockito.atMostOnce())
                .count(ArgumentMatchers.any());
    }

    @Test
    void findAll_withNullPageable_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameUserEntryService.findAll(Mockito.mock(GameUserEntrySpecification.class), null));
    }

    @Test
    void findAll_withNoGameUserEntries_returnsEmptyList() {
        // Arrange
        Mockito.when(gameUserEntryRepository.findAll(ArgumentMatchers.any(GameUserEntrySpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        GameUserEntrySpecification gameUserEntrySpecification = Mockito.mock(GameUserEntrySpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<GameUserEntryDto> result = StreamSupport.stream(gameUserEntryService.findAll(gameUserEntrySpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no paged game user entry results were found.");
    }

    @Test
    void findAll_withGameUserEntries_returnsGameUserEntriesAsGameUserEntryDtos() {
        // Arrange
        Page<GameUserEntry> gameUserEntries = new PageImpl<>(Arrays.asList(new GameUserEntry(), new GameUserEntry()));

        Mockito.when(gameUserEntryRepository.findAll(ArgumentMatchers.any(GameUserEntrySpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(gameUserEntries);

        GameUserEntrySpecification gameUserEntrySpecification = Mockito.mock(GameUserEntrySpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<GameUserEntryDto> result = StreamSupport.stream(gameUserEntryService.findAll(gameUserEntrySpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result shouldn't be empty if the repository returned game user entries.");
    }

    @Test
    void count_withNullGameUserEntrySpecification_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameUserEntryService.count(null));
    }

    @Test
    void count_withValidGameUserEntrySpecification_invokesCount() {
        // Arrange
        Mockito.when(gameUserEntryRepository.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        gameUserEntryService.count(Mockito.mock(GameUserEntrySpecification.class));

        // Assert
        Mockito.verify(gameUserEntryRepository, Mockito.atMostOnce())
                .count(Mockito.any());
    }

    @Test
    void update_withNullGameUserEntryDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameUserEntryService.update(null));
        Mockito.verify(gameUserEntryRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void update_againstDifferentUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(InvalidUserException.class, () -> gameUserEntryService.update(new GameUserEntryDto()));
        Mockito.verify(gameUserEntryRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameUserEntryRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameUserEntryService.update(new GameUserEntryDto()));
        Mockito.verify(gameUserEntryRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void update_withExistingGameUserEntryDto_updatesGameUserEntryDto() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameUserEntryRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameUserEntryRepository.save(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntry());

        // Act
        gameUserEntryService.update(new GameUserEntryDto());

        // Assert
        Mockito.verify(gameUserEntryRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    void patch_withNoGameUserEntryMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameUserEntryService.patch(0L, Mockito.mock(JsonMergePatch.class)));
        Mockito.verify(gameUserEntryRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void patch_withValidIdButInvalidUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new GameUserEntry()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(patchService.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new GameUserEntryDto());

        // Assert
        Assertions.assertThrows(InvalidUserException.class, () -> gameUserEntryService.patch(0L, Mockito.mock(JsonMergePatch.class)));
        Mockito.verify(gameUserEntryRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void patch_withValidId_saveGameUserEntry() {
        // Arrange
        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new GameUserEntry()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(patchService.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new GameUserEntryDto());

        // Act
        gameUserEntryService.patch(0L, Mockito.mock(JsonMergePatch.class));

        // Assert
        Mockito.verify(gameUserEntryRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    void deleteById_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameUserEntryService.deleteById(0L));
    }

    @Test
    void deleteById_withExistingButDifferentUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new GameUserEntry()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(InvalidUserException.class, () -> gameUserEntryService.deleteById(0L));
        Mockito.verify(gameUserEntryRepository, Mockito.never())
                .deleteById(ArgumentMatchers.anyLong());
    }

    @Test
    void deleteById_withExistingId_invokesDeletion() {
        // Arrange
        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new GameUserEntry()));

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.doNothing().when(gameUserEntryRepository)
                .deleteById(ArgumentMatchers.anyLong());

        // Act
        gameUserEntryService.deleteById(0L);

        // Assert
        Mockito.verify(gameUserEntryRepository, Mockito.atMostOnce())
                .deleteById(ArgumentMatchers.anyLong());
    }
}
