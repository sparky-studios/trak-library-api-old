package com.sparky.maidcafe.game.service.impl;

import com.sparky.maidcafe.game.domain.GameGenreXref;
import com.sparky.maidcafe.game.domain.Genre;
import com.sparky.maidcafe.game.repository.GameGenreXrefRepository;
import com.sparky.maidcafe.game.repository.GameRepository;
import com.sparky.maidcafe.game.repository.GenreRepository;
import com.sparky.maidcafe.game.repository.specification.GenreSpecification;
import com.sparky.maidcafe.game.service.dto.GenreDto;
import com.sparky.maidcafe.game.service.mapper.GenreMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ExtendWith(MockitoExtension.class)
public class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameGenreXrefRepository gameGenreXrefRepository;

    @Spy
    private GenreMapper genreMapper = GenreMapper.INSTANCE;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private GenreServiceImpl genreService;

    @Test
    public void save_withNullGenreDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> genreService.save(null));
    }

    @Test
    public void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> genreService.save(new GenreDto()));
    }

    @Test
    public void save_withNewGenreDto_savesGenreDto() {
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
    }

    @Test
    public void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(genreRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> genreService.findById(0L));
    }

    @Test
    public void findById_withValidGenre_returnsGenreDto() {
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

        // Act
        GenreDto result = genreService.findById(0L);

        // Assert
        Assertions.assertEquals(genre.getId(), result.getId(), "The ID does match the entity.");
        Assertions.assertEquals(genre.getName(), result.getName(), "The title does match the entity.");
        Assertions.assertEquals(genre.getDescription(), result.getDescription(), "The description does match the entity.");
        Assertions.assertEquals(genre.getVersion(), result.getVersion(), "The version does match the entity.");
    }

    @Test
    public void findGenresByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> genreService.findGenresByGameId(0L));
    }

    @Test
    public void findGenresByGameId_withNoGenres_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameGenreXrefRepository.findAll(ArgumentMatchers.<Specification<GameGenreXref>>any()))
                .thenReturn(Collections.emptyList());

        // Act
        List<GenreDto> result = StreamSupport.stream(genreService.findGenresByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no genres are returned.");
    }

    @Test
    public void findGenresByGameId_withGenres_returnsGenreDtoList() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Genre genre1 = new Genre();
        genre1.setName("name-1");

        GameGenreXref gameGenreXref1 = new GameGenreXref();
        gameGenreXref1.setGenre(genre1);

        Genre genre2 = new Genre();
        genre2.setName("name-2");

        GameGenreXref gameGenreXref2 = new GameGenreXref();
        gameGenreXref2.setGenre(genre2);

        Mockito.when(gameGenreXrefRepository.findAll(ArgumentMatchers.<Specification<GameGenreXref>>any()))
                .thenReturn(Arrays.asList(gameGenreXref1, gameGenreXref2));

        // Act
        List<GenreDto> result = StreamSupport.stream(genreService.findGenresByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should be not be empty if they're game genre xrefs.");
        Assertions.assertEquals(2, result.size(), "There should be only two genres if there are two xrefs");
    }

    @Test
    public void findAll_withNullPageable_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> genreService.findAll(Mockito.mock(GenreSpecification.class), null));
    }

    @Test
    public void findAll_withNoGenres_returnsEmptyList() {
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
    }

    @Test
    public void findAll_withGenres_returnsGenresAsGenreDtos() {
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
    }

    @Test
    public void update_withNullGenreDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> genreService.update(null));
    }

    @Test
    public void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> genreService.update(new GenreDto()));
    }

    @Test
    public void update_withExistingGenreDto_updatesGenreDto() {
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
    }

    @Test
    public void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(genreRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> genreService.deleteById(0L));
    }

    @Test
    public void delete_withExistingId_invokesDeletion() {
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
