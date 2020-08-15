package com.traklibrary.game.service.impl;

import com.traklibrary.game.domain.Game;
import com.traklibrary.game.domain.GameGenreXref;
import com.traklibrary.game.repository.GameGenreXrefRepository;
import com.traklibrary.game.repository.GameRepository;
import com.traklibrary.game.repository.GenreRepository;
import com.traklibrary.game.repository.specification.GameSpecification;
import com.traklibrary.game.service.dto.GameInfoDto;
import com.traklibrary.game.service.mapper.GameInfoMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ExtendWith(MockitoExtension.class)
public class GameInfoServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GameGenreXrefRepository gameGenreXrefRepository;

    @Spy
    private final GameInfoMapper gameInfoMapper = GameInfoMapper.INSTANCE;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private GameInfoServiceImpl gameInfoService;

    @Test
    public void findByGameId_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameInfoService.findByGameId(0L));
    }

    @Test
    public void findByGameId_withValidGame_returnsGameInfoDto() {
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
        GameInfoDto result = gameInfoService.findByGameId(0L);

        // Assert
        Assertions.assertNotNull(result);
    }

    @Test
    public void findByGenreId_withNonExistentGenre_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameInfoService.findByGenreId(0L, Mockito.mock(Pageable.class)));
    }

    @Test
    public void findByGenreId_withNoGames_returnsEmptyList() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameGenreXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<GameInfoDto> result = StreamSupport.stream(gameInfoService.findByGenreId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(gameInfoMapper, Mockito.never())
                .gameToGameInfoDto(ArgumentMatchers.any());
    }

    @Test
    public void findByGenreId_withMultipleGames_returnsList() {
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
        List<GameInfoDto> result = StreamSupport.stream(gameInfoService.findByGenreId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(gameInfoMapper, Mockito.atMost(2))
                .gameToGameInfoDto(ArgumentMatchers.any());
    }

    @Test
    public void countByGenreId_withNonExistentGenre_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameInfoService.countByGenreId(0L));
    }

    @Test
    public void countByGenreId_withGenre_invokesGameGenreXrefRepository() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameGenreXrefRepository.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        gameInfoService.countByGenreId(0L);

        // Assert
        Mockito.verify(gameGenreXrefRepository, Mockito.atMostOnce())
                .count(ArgumentMatchers.any());
    }

    @Test
    public void findAll_withNullPageable_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameInfoService.findAll(Mockito.mock(GameSpecification.class), null));
    }

    @Test
    public void findAll_withNoGames_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.findAll(ArgumentMatchers.any(GameSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        GameSpecification gameSpecification = Mockito.mock(GameSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<GameInfoDto> result = StreamSupport.stream(gameInfoService.findAll(gameSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no pages game results were found.");
    }

    @Test
    public void findAll_withGames_returnsGamesAsGameInfoDtos() {
        // Arrange
        Page<Game> games = new PageImpl<>(Arrays.asList(new Game(), new Game()));

        Mockito.when(gameRepository.findAll(ArgumentMatchers.any(GameSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(games);

        GameSpecification gameSpecification = Mockito.mock(GameSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<GameInfoDto> result = StreamSupport.stream(gameInfoService.findAll(gameSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result shouldn't be empty if the repository returned games.");
    }

    @Test
    public void count_withNullGameSpecification_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> gameInfoService.count(null));
    }

    @Test
    public void count_withValidGameSpecification_invokesCount() {
        // Arrange
        Mockito.when(gameRepository.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        gameInfoService.count(Mockito.mock(GameSpecification.class));

        // Assert
        Mockito.verify(gameRepository, Mockito.atMostOnce())
                .count(Mockito.any());
    }

}
