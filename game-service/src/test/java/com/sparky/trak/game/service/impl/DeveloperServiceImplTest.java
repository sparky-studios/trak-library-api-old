package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.domain.Developer;
import com.sparky.trak.game.domain.GameDeveloperXref;
import com.sparky.trak.game.repository.DeveloperRepository;
import com.sparky.trak.game.repository.GameDeveloperXrefRepository;
import com.sparky.trak.game.repository.GameRepository;
import com.sparky.trak.game.repository.specification.DeveloperSpecification;
import com.sparky.trak.game.service.PatchService;
import com.sparky.trak.game.service.dto.DeveloperDto;
import com.sparky.trak.game.service.mapper.DeveloperMapper;
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
public class DeveloperServiceImplTest {

    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameDeveloperXrefRepository gameDeveloperXrefRepository;

    @Mock
    private MessageSource messageSource;

    @Spy
    private DeveloperMapper developerMapper = DeveloperMapper.INSTANCE;

    @Mock
    private PatchService patchService;

    @InjectMocks
    private DeveloperServiceImpl developerService;

    @Test
    public void save_withNullDeveloperDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> developerService.save(null));
    }

    @Test
    public void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> developerService.save(new DeveloperDto()));
    }

    @Test
    public void save_withNewDeveloperDto_savesDeveloperDto() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(developerRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Developer());

        // Act
        developerService.save(new DeveloperDto());

        // Assert
        Mockito.verify(developerRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(developerRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> developerService.findById(0L));
    }

    @Test
    public void findById_withValidDeveloper_returnsDeveloperDto() {
        // Arrange
        Developer developer = new Developer();
        developer.setId(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(developerRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(developer));

        // Act
        DeveloperDto result = developerService.findById(0L);

        // Assert
        Assertions.assertNotNull(result, "The mapped result should not be null.");
    }

    @Test
    public void findDevelopersByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> developerService.findDevelopersByGameId(0L, Mockito.mock(Pageable.class)));
    }

    @Test
    public void findDevelopersByGameId_withNoDevelopers_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gameDeveloperXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<DeveloperDto> result = StreamSupport.stream(developerService.findDevelopersByGameId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(developerMapper, Mockito.never())
                .developerToDeveloperDto(ArgumentMatchers.any());
    }

    @Test
    public void findDevelopersByGameId_withMultipleDevelopers_returnsList() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        GameDeveloperXref gameDeveloperXref1 = new GameDeveloperXref();
        gameDeveloperXref1.setDeveloper(new Developer());

        GameDeveloperXref gameDeveloperXref2 = new GameDeveloperXref();
        gameDeveloperXref2.setDeveloper(new Developer());

        Mockito.when(gameDeveloperXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(gameDeveloperXref1, gameDeveloperXref2)));

        // Act
        List<DeveloperDto> result = StreamSupport.stream(developerService.findDevelopersByGameId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(developerMapper, Mockito.atMost(2))
                .developerToDeveloperDto(ArgumentMatchers.any());
    }

    @Test
    public void findAll_withNullPageable_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> developerService.findAll(Mockito.mock(DeveloperSpecification.class), null));
    }

    @Test
    public void findAll_withNoCompanies_returnsEmptyList() {
        // Arrange
        Mockito.when(developerRepository.findAll(ArgumentMatchers.any(DeveloperSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        DeveloperSpecification developerSpecification = Mockito.mock(DeveloperSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<DeveloperDto> result = StreamSupport.stream(developerService.findAll(developerSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no paged developer results were found.");
    }

    @Test
    public void findAll_withCompanies_returnsCompaniesAsDeveloperDtos() {
        // Arrange
        Page<Developer> companies = new PageImpl<>(Arrays.asList(new Developer(), new Developer()));

        Mockito.when(developerRepository.findAll(ArgumentMatchers.any(DeveloperSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(companies);

        DeveloperSpecification developerSpecification = Mockito.mock(DeveloperSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<DeveloperDto> result = StreamSupport.stream(developerService.findAll(developerSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result shouldn't be empty if the repository returned companies.");
    }

    @Test
    public void update_withNullDeveloperDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> developerService.update(null));
    }

    @Test
    public void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> developerService.update(new DeveloperDto()));
    }

    @Test
    public void update_withExistingDeveloperDto_updatesDeveloperDto() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(developerRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Developer());

        // Act
        developerService.update(new DeveloperDto());

        // Assert
        Mockito.verify(developerRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void patch_withNoDeveloperMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(developerRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> developerService.patch(0L, Mockito.mock(JsonMergePatch.class)));
    }

    @Test
    public void patch_withValidId_savesDeveloper() {
        // Arrange
        Mockito.when(developerRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Developer()));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(patchService.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new DeveloperDto());

        // Act
        developerService.patch(0L, Mockito.mock(JsonMergePatch.class));

        // Assert
        Mockito.verify(developerRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> developerService.deleteById(0L));
    }

    @Test
    public void delete_withExistingId_invokesDeletion() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.doNothing().when(developerRepository)
                .deleteById(ArgumentMatchers.anyLong());

        // Act
        developerService.deleteById(0L);

        // Assert
        Mockito.verify(developerRepository, Mockito.atMostOnce())
                .deleteById(ArgumentMatchers.anyLong());
    }
}
