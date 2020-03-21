package com.sparky.maidcafe.game.service.impl;

import com.sparky.maidcafe.game.domain.Console;
import com.sparky.maidcafe.game.domain.Game;
import com.sparky.maidcafe.game.domain.GameUserEntry;
import com.sparky.maidcafe.game.domain.GameUserEntryStatus;
import com.sparky.maidcafe.game.repository.GameUserEntryRepository;
import com.sparky.maidcafe.game.repository.specification.GameUserEntrySpecification;
import com.sparky.maidcafe.game.service.PatchService;
import com.sparky.maidcafe.game.service.dto.GameUserEntryDto;
import com.sparky.maidcafe.game.service.mapper.GameUserEntryMapper;
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
public class GameUserEntryServiceImplTest {

    @Mock
    private GameUserEntryRepository gameUserEntryRepository;

    @Spy
    private GameUserEntryMapper gameUserEntryMapper = GameUserEntryMapper.INSTANCE;

    @Mock
    private MessageSource messageSource;

    @Mock
    private PatchService patchService;

    @InjectMocks
    private GameUserEntryServiceImpl gameUserEntryService;

    @Test
    public void save_withNullGameUserEntryDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameUserEntryService.save(null));
    }

    @Test
    public void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(gameUserEntryRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> gameUserEntryService.save(new GameUserEntryDto()));
    }

    @Test
    public void save_withNewGameUserEntryDto_savesGameUserEntryDto() {
        // Arrange
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
    public void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameUserEntryService.findById(0L));
    }

    @Test
    public void findById_withValidGameUserEntry_returnsGameUserEntryDto() {
        // Arrange
        Game game = new Game();
        game.setTitle("test-title");

        Console console = new Console();
        console.setName("test-name");

        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setId(1L);
        gameUserEntry.setGameId(2L);
        gameUserEntry.setGame(game);
        gameUserEntry.setConsoleId(3L);
        gameUserEntry.setConsole(console);
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
        Assertions.assertEquals(gameUserEntry.getGame().getTitle(), result.getGameName(), "The mapped game name does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getConsoleId(), result.getConsoleId(), "The mapped console ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getConsole().getName(), result.getConsoleName(), "The mapped console name does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getUserId(), result.getUserId(), "The mapped user ID does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getStatus(), result.getStatus(), "The mapped status does not match the entity.");
        Assertions.assertEquals(gameUserEntry.getRating(), result.getRating(), "The mapped rating does not match the entity.");
    }

    @Test
    public void findAll_withNullPageable_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameUserEntryService.findAll(Mockito.mock(GameUserEntrySpecification.class), null));
    }

    @Test
    public void findAll_withNoGameUserEntries_returnsEmptyList() {
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
    public void findAll_withGameUserEntries_returnsGameUserEntriesAsGameUserEntryDtos() {
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
    public void update_withNullGameUserEntryDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameUserEntryService.update(null));
    }

    @Test
    public void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameUserEntryRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameUserEntryService.update(new GameUserEntryDto()));
    }

    @Test
    public void update_withExistingGameUserEntryDto_updatesGameUserEntryDto() {
        // Arrange
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
    public void patch_withNoGameUserEntryMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameUserEntryService.patch(0L, Mockito.mock(JsonMergePatch.class)));
    }

    @Test
    public void patch_withValidId_saveGameUserEntry() {
        // Arrange
        Mockito.when(gameUserEntryRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new GameUserEntry()));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(patchService.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new GameUserEntryDto());

        // Act
        gameUserEntryService.patch(0L, Mockito.mock(JsonMergePatch.class));

        // Assert
        Mockito.verify(gameUserEntryRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameUserEntryRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameUserEntryService.deleteById(0L));
    }

    @Test
    public void delete_withExistingId_invokesDeletion() {
        // Arrange
        Mockito.when(gameUserEntryRepository.existsById(ArgumentMatchers.anyLong()))
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
