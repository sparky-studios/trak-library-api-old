package com.traklibrary.game.service.impl;

import com.traklibrary.game.domain.Developer;
import com.traklibrary.game.domain.Game;
import com.traklibrary.game.repository.DeveloperRepository;
import com.traklibrary.game.repository.GameRepository;
import com.traklibrary.game.repository.specification.DeveloperSpecification;
import com.traklibrary.game.service.PatchService;
import com.traklibrary.game.service.dto.DeveloperDto;
import com.traklibrary.game.service.mapper.DeveloperMapper;
import com.traklibrary.game.service.mapper.GameMappers;
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

import javax.json.JsonMergePatch;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ExtendWith(MockitoExtension.class)
class DeveloperServiceImplTest {

    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private MessageSource messageSource;

    @Spy
    private final DeveloperMapper developerMapper = GameMappers.DEVELOPER_MAPPER;

    @Mock
    private PatchService patchService;

    @InjectMocks
    private DeveloperServiceImpl developerService;

    @Test
    void save_withNullDeveloperDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> developerService.save(null));
    }

    @Test
    void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        DeveloperDto developerDto = new DeveloperDto();

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> developerService.save(developerDto));
    }

    @Test
    void save_withNewDeveloperDto_savesDeveloperDto() {
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
    void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(developerRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> developerService.findById(0L));
    }

    @Test
    void findById_withValidDeveloper_returnsDeveloperDto() {
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
    void findDevelopersByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> developerService.findDevelopersByGameId(0L));
    }

    @Test
    void findDevelopersByGameId_withNoDevelopers_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Game()));

        // Act
        List<DeveloperDto> result = StreamSupport.stream(developerService.findDevelopersByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(developerMapper, Mockito.never())
                .developerToDeveloperDto(ArgumentMatchers.any());
    }

    @Test
    void findDevelopersByGameId_withMultipleDevelopers_returnsList() {
        // Arrange
        Developer developer1 = new Developer();
        developer1.setName("developer-1");

        Developer developer2 = new Developer();
        developer2.setName("developer-2");

        Game game = new Game();
        game.addDeveloper(developer1);
        game.addDeveloper(developer2);

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        // Act
        List<DeveloperDto> result = StreamSupport.stream(developerService.findDevelopersByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");
        Assertions.assertEquals(2, result.size(), "There should be only two developers if there are two developers associated with the game.");

        Mockito.verify(developerMapper, Mockito.atMost(2))
                .developerToDeveloperDto(ArgumentMatchers.any());
    }

    @Test
    void findAll_withNullPageable_throwsNullPointerException() {
        // Arrange
        DeveloperSpecification developerSpecification = Mockito.mock(DeveloperSpecification.class);

        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> developerService.findAll(developerSpecification, null));
    }

    @Test
    void findAll_withNoCompanies_returnsEmptyList() {
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
    void findAll_withCompanies_returnsCompaniesAsDeveloperDtos() {
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
    void count_withNullDeveloperSpecification_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> developerService.count(null));
    }

    @Test
    void count_withValidDeveloperSpecification_invokesCount() {
        // Arrange
        Mockito.when(developerRepository.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        developerService.count(Mockito.mock(DeveloperSpecification.class));

        // Assert
        Mockito.verify(developerRepository, Mockito.atMostOnce())
                .count(Mockito.any());
    }

    @Test
    void update_withNullDeveloperDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> developerService.update(null));
    }

    @Test
    void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        DeveloperDto developerDto = new DeveloperDto();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> developerService.update(developerDto));
    }

    @Test
    void update_withExistingDeveloperDto_updatesDeveloperDto() {
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
    void patch_withNoDeveloperMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(developerRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        JsonMergePatch jsonMergePatch = Mockito.mock(JsonMergePatch.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> developerService.patch(0L, jsonMergePatch));
    }

    @Test
    void patch_withValidId_savesDeveloper() {
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
    void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(developerRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> developerService.deleteById(0L));
    }

    @Test
    void delete_withExistingId_invokesDeletion() {
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
