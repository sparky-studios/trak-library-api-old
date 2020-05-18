package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.domain.GamePlatformXref;
import com.sparky.trak.game.domain.Platform;
import com.sparky.trak.game.repository.GamePlatformXrefRepository;
import com.sparky.trak.game.repository.GameRepository;
import com.sparky.trak.game.repository.PlatformRepository;
import com.sparky.trak.game.repository.specification.PlatformSpecification;
import com.sparky.trak.game.service.PatchService;
import com.sparky.trak.game.service.dto.PlatformDto;
import com.sparky.trak.game.service.mapper.PlatformMapper;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ExtendWith(MockitoExtension.class)
public class PlatformServiceImplTest {

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GamePlatformXrefRepository gamePlatformXrefRepository;

    @Spy
    private PlatformMapper platformMapper = PlatformMapper.INSTANCE;

    @Mock
    private MessageSource messageSource;

    @Mock
    private PatchService patchService;

    @InjectMocks
    private PlatformServiceImpl platformService;

    @Test
    public void save_withNullPlatformDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> platformService.save(null));
    }

    @Test
    public void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> platformService.save(new PlatformDto()));
    }

    @Test
    public void save_withNewPlatformDto_savesPlatformDto() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(platformRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Platform());

        // Act
        platformService.save(new PlatformDto());

        // Assert
        Mockito.verify(platformRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(platformRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> platformService.findById(0L));
    }

    @Test
    public void findById_withValidPlatform_returnsPlatformDto() {
        // Arrange
        Platform platform = new Platform();
        platform.setId(1L);
        platform.setName("test-name");
        platform.setDescription("test-description");
        platform.setReleaseDate(LocalDate.now());
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
        Assertions.assertEquals(platform.getReleaseDate(), result.getReleaseDate(), "The release date does match the entity.");
        Assertions.assertEquals(platform.getVersion(), result.getVersion(), "The version does match the entity.");
    }

    @Test
    public void findPlatformsByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> platformService.findPlatformsByGameId(0L, Mockito.mock(Pageable.class)));
    }

    @Test
    public void findPlatformsByGameId_withNoPlatforms_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gamePlatformXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        List<PlatformDto> result = StreamSupport.stream(platformService.findPlatformsByGameId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no platforms are returned.");

        Mockito.verify(platformMapper, Mockito.never())
                .platformToPlatformDto(ArgumentMatchers.any());
    }

    @Test
    public void findPlatformsByGameId_withMultiplePlatforms_returnsList() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        GamePlatformXref gamePlatformXref1 = new GamePlatformXref();
        gamePlatformXref1.setPlatform(new Platform());

        GamePlatformXref gamePlatformXref2 = new GamePlatformXref();
        gamePlatformXref2.setPlatform(new Platform());

        Mockito.when(gamePlatformXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(gamePlatformXref1, gamePlatformXref2)));

        // Act
        List<PlatformDto> result = StreamSupport.stream(platformService.findPlatformsByGameId(0L, Mockito.mock(Pageable.class)).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");

        Mockito.verify(platformMapper, Mockito.atMost(2))
                .platformToPlatformDto(ArgumentMatchers.any());
    }

    @Test
    public void countPlatformsByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> platformService.countPlatformsByGameId(0L));
    }

    @Test
    public void countPlatformsByGameId_withGame_invokesGamePlatformXrefRepository() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gamePlatformXrefRepository.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        platformService.countPlatformsByGameId(0L);

        // Assert
        Mockito.verify(gamePlatformXrefRepository, Mockito.atMostOnce())
                .count(ArgumentMatchers.any());
    }

    @Test
    public void findAll_withNoPlatformsAndNoPageable_returnsEmptyList() {
        // Arrange
        Mockito.when(platformRepository.findAll())
                .thenReturn(Collections.emptyList());

        // Act
        List<PlatformDto> result = StreamSupport.stream(platformService.findAll().spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "There should be no platform dto's if no platforms were found.");

        Mockito.verify(platformMapper, Mockito.never())
                .platformToPlatformDto(ArgumentMatchers.any());
    }

    @Test
    public void findAll_withPlatformsAndNoPageable_returnsListOfPlatformDtos() {
        // Arrange
        Mockito.when(platformRepository.findAll())
                .thenReturn(Arrays.asList(new Platform(), new Platform()));

        // Act
        List<PlatformDto> result = StreamSupport.stream(platformService.findAll().spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "There should be platform DTO's if platforms were found.");

        Mockito.verify(platformMapper, Mockito.atMost(2))
                .platformToPlatformDto(ArgumentMatchers.any());
    }

    @Test
    public void findAll_withNullPageable_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> platformService.findAll(Mockito.mock(PlatformSpecification.class), null));
    }

    @Test
    public void findAll_withNoPlatforms_returnsEmptyList() {
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
    public void findAll_withPlatforms_returnsPlatformsAsPlatformDtos() {
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
    public void count_withNullPlatformSpecification_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> platformService.count(null));
    }

    @Test
    public void count_withPlatformSpecification_invokesCount() {
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
    public void update_withNullPlatformDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> platformService.update(null));
    }

    @Test
    public void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> platformService.update(new PlatformDto()));
    }

    @Test
    public void update_withExistingPlatformDto_updatesPlatformDto() {
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
    public void patch_withNoPlatformMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(platformRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> platformService.patch(0L, Mockito.mock(JsonMergePatch.class)));
    }

    @Test
    public void patch_withValidId_savesPlatform() {
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
    public void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(platformRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> platformService.deleteById(0L));
    }

    @Test
    public void delete_withExistingId_invokesDeletion() {
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
