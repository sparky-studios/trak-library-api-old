package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.GameUserEntry;
import com.sparkystudios.traklibrary.game.domain.GameUserEntryPlatform;
import com.sparkystudios.traklibrary.game.domain.Platform;
import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.repository.GameUserEntryRepository;
import com.sparkystudios.traklibrary.game.repository.PlatformRepository;
import com.sparkystudios.traklibrary.game.repository.specification.GameUserEntrySpecification;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import com.sparkystudios.traklibrary.game.service.dto.request.GameUserEntryRequest;
import com.sparkystudios.traklibrary.game.service.mapper.GameMappers;
import com.sparkystudios.traklibrary.game.service.mapper.GameUserEntryMapper;
import com.sparkystudios.traklibrary.security.AuthenticationService;
import com.sparkystudios.traklibrary.security.exception.InvalidUserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ExtendWith(MockitoExtension.class)
class GameUserEntryServiceImplTest {

    @Mock
    private GameUserEntryRepository gameUserEntryRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlatformRepository platformRepository;

    @Spy
    private final GameUserEntryMapper gameUserEntryMapper = GameMappers.GAME_USER_ENTRY_MAPPER;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private MessageSource messageSource;

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

        GameUserEntryRequest gameUserEntryRequest = new GameUserEntryRequest();

        // Assert
        Assertions.assertThrows(InvalidUserException.class, () -> gameUserEntryService.save(gameUserEntryRequest));
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

        GameUserEntryRequest gameUserEntryRequest = new GameUserEntryRequest();


        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> gameUserEntryService.save(gameUserEntryRequest));
        Mockito.verify(gameUserEntryRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void save_withNewGameUserEntryRequestWithNoPlatforms_savesGameUserEntry() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameUserEntryRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(gameUserEntryRepository.save(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntry());

        GameUserEntryRequest gameUserEntryRequest = new GameUserEntryRequest();
        gameUserEntryRequest.setPlatformIds(Collections.emptyList());

        // Act
        gameUserEntryService.save(gameUserEntryRequest);

        // Assert
        Mockito.verify(gameUserEntryRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    void save_withNewGameUserEntryRequestWithPlatformIds_savesGameUserEntryAndInvokesPlatformRepository() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameUserEntryRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(gameUserEntryRepository.save(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntry());

        Mockito.when(platformRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        GameUserEntryRequest gameUserEntryRequest = Mockito.spy(GameUserEntryRequest.class);
        gameUserEntryRequest.setPlatformIds(List.of(1L, 2L));

        // Act
        gameUserEntryService.save(gameUserEntryRequest);

        // Assert
        Mockito.verify(platformRepository, Mockito.atMost(2))
                .findById(ArgumentMatchers.anyLong());

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
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new GameUserEntry()));

        // Act
        GameUserEntryDto result = gameUserEntryService.findById(0L);

        // Assert
        Assertions.assertNotNull(result, "The mapped result should not be null.");
    }

    @Test
    void findGameUserEntriesByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Pageable pageable = Mockito.mock(Pageable.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameUserEntryService.findGameUserEntriesByGameId(0L, null, pageable));
    }

    @Test
    void findGameUserEntriesByGameId_withNoGameUserEntries_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameUserEntryRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<GameUserEntryDto> result = StreamSupport.stream(gameUserEntryService.findGameUserEntriesByGameId(0L, null, Mockito.mock(Pageable.class)).spliterator(), false)
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

        Mockito.when(gameUserEntryRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(new GameUserEntry(), new GameUserEntry())));

        // Act
        List<GameUserEntryDto> result = StreamSupport.stream(gameUserEntryService.findGameUserEntriesByGameId(0L, null, Mockito.mock(Pageable.class)).spliterator(), false)
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
        // Arrange
        GameUserEntrySpecification gameUserEntrySpecification = Mockito.mock(GameUserEntrySpecification.class);

        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameUserEntryService.findAll(gameUserEntrySpecification, null));
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

        GameUserEntryRequest gameUserEntryRequest = new GameUserEntryRequest();

        // Assert
        Assertions.assertThrows(InvalidUserException.class, () -> gameUserEntryService.update(gameUserEntryRequest));
        Mockito.verify(gameUserEntryRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        GameUserEntryRequest gameUserEntryRequest = new GameUserEntryRequest();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameUserEntryService.update(gameUserEntryRequest));
        Mockito.verify(gameUserEntryRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void update_withGameUserEntryWithNoPlatforms_savesGameUserEntry() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new GameUserEntry()));

        GameUserEntryRequest gameUserEntryRequest = new GameUserEntryRequest();
        gameUserEntryRequest.setPlatformIds(Collections.emptyList());

        Mockito.when(gameUserEntryRepository.save(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntry());

        // Act
        gameUserEntryService.update(gameUserEntryRequest);

        // Assert
        Mockito.verify(platformRepository, Mockito.never())
                .findById(ArgumentMatchers.anyLong());

        Mockito.verify(gameUserEntryRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    void update_withGameUserEntryWithDifferentPlatforms_savesGameUserEntry() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Platform platform1 = new Platform();
        platform1.setId(1L);

        GameUserEntryPlatform gameUserEntryPlatform1 = new GameUserEntryPlatform();
        gameUserEntryPlatform1.setPlatformId(platform1.getId());
        gameUserEntryPlatform1.setPlatform(platform1);

        Platform platform2 = new Platform();
        platform2.setId(2L);

        GameUserEntryPlatform gameUserEntryPlatform2 = new GameUserEntryPlatform();
        gameUserEntryPlatform2.setPlatformId(platform2.getId());
        gameUserEntryPlatform2.setPlatform(platform2);

        List<GameUserEntryPlatform> platforms = new ArrayList<>();
        platforms.add(gameUserEntryPlatform1);
        platforms.add(gameUserEntryPlatform2);

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setGameUserEntryPlatforms(platforms);

        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(gameUserEntry));

        Mockito.when(gameUserEntryRepository.save(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntry());

        List<Long> platformIds = new ArrayList<>();
        platformIds.add(1L);
        platformIds.add(3L);

        GameUserEntryRequest gameUserEntryRequest = new GameUserEntryRequest();
        gameUserEntryRequest.setPlatformIds(platformIds);

        Mockito.when(platformRepository.findById(ArgumentMatchers.eq(3L)))
                .thenReturn(Optional.of(new Platform()));

        // Act
        gameUserEntryService.update(gameUserEntryRequest);

        // Assert
        Mockito.verify(platformRepository, Mockito.atMostOnce())
                .findById(ArgumentMatchers.anyLong());

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
