package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.Genre;
import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.repository.GenreRepository;
import com.sparkystudios.traklibrary.game.repository.specification.GenreSpecification;
import com.sparkystudios.traklibrary.game.service.PatchService;
import com.sparkystudios.traklibrary.game.service.dto.GenreDto;
import com.sparkystudios.traklibrary.game.service.mapper.GenreMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GenreMapper genreMapper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private PatchService patchService;

    @InjectMocks
    private GenreServiceImpl genreService;

    @Test
    void save_withNullGenreDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> genreService.save(null));
    }

    @Test
    void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        GenreDto genreDto = new GenreDto();

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> genreService.save(genreDto));
    }

    @Test
    void save_withNewGenreDto_savesGenreDto() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(genreRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Genre());

        // Act
        genreService.save(new GenreDto());

        // Assert
        Mockito.verify(genreRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(genreMapper, Mockito.atMostOnce())
                .fromGenre(ArgumentMatchers.any());
    }

    @Test
    void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(genreRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> genreService.findById(0L));
    }

    @Test
    void findById_withValidGenre_returnsGenreDto() {
        // Arrange
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("test-name");
        genre.setDescription("test-description");
        genre.setVersion(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(genreRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(genre));

        Mockito.when(genreMapper.fromGenre(ArgumentMatchers.any()))
                .thenReturn(new GenreDto());

        // Act
        GenreDto result = genreService.findById(0L);

        // Assert
        Assertions.assertNotNull(result, "The mapped result should not be null.");

        Mockito.verify(genreMapper, Mockito.atMostOnce())
                .fromGenre(ArgumentMatchers.any());
    }

    @Test
    void findGenresByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> genreService.findGenresByGameId(0L));
    }

    @Test
    void findGenresByGameId_withNoGenres_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Game()));

        // Act
        List<GenreDto> result = StreamSupport.stream(genreService.findGenresByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no genres are returned.");

        Mockito.verify(genreMapper, Mockito.atMostOnce())
                .fromGenre(ArgumentMatchers.any());
    }

    @Test
    void findGenresByGameId_withGenres_returnsGenreDtoList() {
        // Arrange
        Genre genre1 = new Genre();
        genre1.setName("genre-1");

        Genre genre2 = new Genre();
        genre2.setName("genre-2");

        Game game = new Game();
        game.addGenre(genre1);
        game.addGenre(genre2);

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        GenreDto genreDto1 = new GenreDto();
        genreDto1.setName(genre1.getName());

        Mockito.when(genreMapper.fromGenre(genre1))
                .thenReturn(genreDto1);

        GenreDto genreDto2 = new GenreDto();
        genreDto2.setName(genre2.getName());

        Mockito.when(genreMapper.fromGenre(genre2))
                .thenReturn(genreDto2);

        // Act
        List<GenreDto> result = StreamSupport.stream(genreService.findGenresByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should be not be empty if they're game genres.");
        Assertions.assertEquals(2, result.size(), "There should be only two genres.");

        Mockito.verify(genreMapper, Mockito.atMost(2))
                .fromGenre(ArgumentMatchers.any());
    }

    @Test
    void findAll_withNullPageable_throwsNullPointerException() {
        // Arrange
        GenreSpecification genreSpecification = Mockito.mock(GenreSpecification.class);

        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> genreService.findAll(genreSpecification, null));
    }

    @Test
    void findAll_withNoGenres_returnsEmptyList() {
        // Arrange
        Mockito.when(genreRepository.findAll(ArgumentMatchers.any(GenreSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        GenreSpecification genreSpecification = Mockito.mock(GenreSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<GenreDto> result = StreamSupport.stream(genreService.findAll(genreSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no paged genre results were found.");

        Mockito.verify(genreMapper, Mockito.never())
                .fromGenre(ArgumentMatchers.any());
    }

    @Test
    void findAll_withGenres_returnsGenresAsGenreDtos() {
        // Arrange
        Page<Genre> genres = new PageImpl<>(Arrays.asList(new Genre(), new Genre()));

        Mockito.when(genreRepository.findAll(ArgumentMatchers.any(GenreSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(genres);

        GenreSpecification genreSpecification = Mockito.mock(GenreSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<GenreDto> result = StreamSupport.stream(genreService.findAll(genreSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result shouldn't be empty if the repository returned genres.");

        Mockito.verify(genreMapper, Mockito.atMost(2))
                .fromGenre(ArgumentMatchers.any());
    }

    @Test
    void count_withNullGenreSpecification_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> genreService.count(null));
    }

    @Test
    void count_withValidGameUserEntrySpecification_invokesCount() {
        // Arrange
        Mockito.when(genreRepository.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        genreService.count(Mockito.mock(GenreSpecification.class));

        // Assert
        Mockito.verify(genreRepository, Mockito.atMostOnce())
                .count(Mockito.any());
    }

    @Test
    void update_withNullGenreDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> genreService.update(null));
    }

    @Test
    void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        GenreDto genreDto = new GenreDto();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> genreService.update(genreDto));
    }

    @Test
    void update_withExistingGenreDto_updatesGenreDto() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(genreRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Genre());

        // Act
        genreService.update(new GenreDto());

        // Assert
        Mockito.verify(genreRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(genreMapper, Mockito.atMostOnce())
                .fromGenre(ArgumentMatchers.any());
    }

    @Test
    void patch_withNoGenreMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(genreRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        JsonMergePatch jsonMergePatch = Mockito.mock(JsonMergePatch.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> genreService.patch(0L, jsonMergePatch));
    }

    @Test
    void patch_withValidId_saveGenre() {
        // Arrange
        Mockito.when(genreRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Genre()));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(patchService.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new GenreDto());

        // Act
        genreService.patch(0L, Mockito.mock(JsonMergePatch.class));

        // Assert
        Mockito.verify(genreRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(genreMapper, Mockito.atMost(2))
                .fromGenre(ArgumentMatchers.any());
    }

    @Test
    void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> genreService.deleteById(0L));
    }

    @Test
    void delete_withExistingId_invokesDeletion() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.doNothing().when(genreRepository)
                .deleteById(ArgumentMatchers.anyLong());

        // Act
        genreService.deleteById(0L);

        // Assert
        Mockito.verify(genreRepository, Mockito.atMostOnce())
                .deleteById(ArgumentMatchers.anyLong());
    }
}
