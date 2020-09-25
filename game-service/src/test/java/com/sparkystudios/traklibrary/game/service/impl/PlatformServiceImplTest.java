package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.Platform;
import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.repository.PlatformRepository;
import com.sparkystudios.traklibrary.game.repository.specification.PlatformSpecification;
import com.sparkystudios.traklibrary.game.service.PatchService;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import com.sparkystudios.traklibrary.game.service.dto.PlatformDto;
import com.sparkystudios.traklibrary.game.service.dto.PlatformReleaseDateDto;
import com.sparkystudios.traklibrary.game.service.mapper.GameMappers;
import com.sparkystudios.traklibrary.game.service.mapper.PlatformMapper;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ExtendWith(MockitoExtension.class)
class PlatformServiceImplTest {

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private GameRepository gameRepository;

    @Spy
    private final PlatformMapper platformMapper = GameMappers.PLATFORM_MAPPER;

    @Mock
    private MessageSource messageSource;

    @Mock
    private PatchService patchService;

    @InjectMocks
    private PlatformServiceImpl platformService;

    @Test
    void save_withNullPlatformDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> platformService.save(null));
    }

    @Test
    void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        PlatformDto platformDto = new PlatformDto();

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> platformService.save(platformDto));
    }

    @Test
    void save_withNewPlatformDto_savesPlatformDto() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(platformRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Platform());

        PlatformDto platformDto = new PlatformDto();
        platformDto.getReleaseDates().add(new PlatformReleaseDateDto());
        platformDto.getReleaseDates().add(new PlatformReleaseDateDto());

        // Act
        platformService.save(platformDto);

        // Assert
        Mockito.verify(platformRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(platformRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> platformService.findById(0L));
    }

    @Test
    void findById_withValidPlatform_returnsPlatformDto() {
        // Arrange
        Platform platform = new Platform();
        platform.setId(1L);
        platform.setName("test-name");
        platform.setDescription("test-description");
        platform.setVersion(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(platformRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(platform));

        // Act
        PlatformDto result = platformService.findById(0L);

        // Assert
        Assertions.assertEquals(platform.getId(), result.getId(), "The ID does match the entity.");
        Assertions.assertEquals(platform.getName(), result.getName(), "The name does match the entity.");
        Assertions.assertEquals(platform.getDescription(), result.getDescription(), "The description does match the entity.");
        Assertions.assertEquals(platform.getVersion(), result.getVersion(), "The version does match the entity.");
    }

    @Test
    void findPlatformsByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> platformService.findPlatformsByGameId(0L));
    }

    @Test
    void findPlatformsByGameId_withNoPlatforms_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Game()));

        // Act
        List<PlatformDto> result = StreamSupport.stream(platformService.findPlatformsByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no platforms are returned.");

        Mockito.verify(platformMapper, Mockito.never())
                .platformToPlatformDto(ArgumentMatchers.any());
    }

    @Test
    void findPlatformsByGameId_withPlatforms_returnsList() {
        // Arrange
        Platform platform1 = new Platform();
        platform1.setName("platform-1");

        Platform platform2 = new Platform();
        platform2.setName("platform-2");

        Game game = new Game();
        game.addPlatform(platform1);
        game.addPlatform(platform2);

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        // Act
        List<PlatformDto> result = StreamSupport.stream(platformService.findPlatformsByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");
        Assertions.assertEquals(2, result.size(), "There should be only two platforms.");

        Mockito.verify(platformMapper, Mockito.atMost(2))
                .platformToPlatformDto(ArgumentMatchers.any());
    }

    @Test
    void findAll_withNullPageable_throwsNullPointerException() {
        // Arrange
        PlatformSpecification platformSpecification = Mockito.mock(PlatformSpecification.class);

        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> platformService.findAll(platformSpecification, null));
    }

    @Test
    void findAll_withNoPlatforms_returnsEmptyList() {
        // Arrange
        Mockito.when(platformRepository.findAll(ArgumentMatchers.any(PlatformSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        PlatformSpecification platformSpecification = Mockito.mock(PlatformSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<PlatformDto> result = StreamSupport.stream(platformService.findAll(platformSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no pages platform results were found.");
    }

    @Test
    void findAll_withPlatforms_returnsPlatformsAsPlatformDtos() {
        // Arrange
        Page<Platform> platforms = new PageImpl<>(Arrays.asList(new Platform(), new Platform()));

        Mockito.when(platformRepository.findAll(ArgumentMatchers.any(PlatformSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(platforms);

        PlatformSpecification platformSpecification = Mockito.mock(PlatformSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<PlatformDto> result = StreamSupport.stream(platformService.findAll(platformSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result shouldn't be empty if the repository returned platforms.");
    }

    @Test
    void count_withNullPlatformSpecification_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> platformService.count(null));
    }

    @Test
    void count_withPlatformSpecification_invokesCount() {
        // Arrange
        Mockito.when(platformRepository.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        platformService.count(Mockito.mock(PlatformSpecification.class));

        // Assert
        Mockito.verify(platformRepository, Mockito.atMostOnce())
                .count(Mockito.any());
    }

    @Test
    void update_withNullPlatformDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> platformService.update(null));
    }

    @Test
    void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        PlatformDto platformDto = new PlatformDto();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> platformService.update(platformDto));
    }

    @Test
    void update_withExistingPlatformDto_updatesPlatformDto() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(platformRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Platform());

        // Act
        platformService.update(new PlatformDto());

        // Assert
        Mockito.verify(platformRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    void patch_withNoPlatformMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(platformRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        JsonMergePatch jsonMergePatch = Mockito.mock(JsonMergePatch.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> platformService.patch(0L, jsonMergePatch));
    }

    @Test
    void patch_withValidId_savesPlatform() {
        // Arrange
        Mockito.when(platformRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Platform()));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(patchService.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new PlatformDto());

        // Act
        platformService.patch(0L, Mockito.mock(JsonMergePatch.class));

        // Assert
        Mockito.verify(platformRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> platformService.deleteById(0L));
    }

    @Test
    void delete_withExistingId_invokesDeletion() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.doNothing().when(platformRepository)
                .deleteById(ArgumentMatchers.anyLong());

        // Act
        platformService.deleteById(0L);

        // Assert
        Mockito.verify(platformRepository, Mockito.atMostOnce())
                .deleteById(ArgumentMatchers.anyLong());
    }
}
