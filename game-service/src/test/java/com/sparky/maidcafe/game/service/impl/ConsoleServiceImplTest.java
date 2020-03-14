package com.sparky.maidcafe.game.service.impl;

import com.sparky.maidcafe.game.domain.Console;
import com.sparky.maidcafe.game.domain.Game;
import com.sparky.maidcafe.game.repository.ConsoleRepository;
import com.sparky.maidcafe.game.repository.specification.ConsoleSpecification;
import com.sparky.maidcafe.game.service.PatchService;
import com.sparky.maidcafe.game.service.dto.ConsoleDto;
import com.sparky.maidcafe.game.service.dto.GameDto;
import com.sparky.maidcafe.game.service.mapper.ConsoleMapper;
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
public class ConsoleServiceImplTest {

    @Mock
    private ConsoleRepository consoleRepository;

    @Spy
    private ConsoleMapper consoleMapper = ConsoleMapper.INSTANCE;

    @Mock
    private MessageSource messageSource;

    @Mock
    private PatchService patchService;

    @InjectMocks
    private ConsoleServiceImpl consoleService;

    @Test
    public void save_withNullConsoleDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> consoleService.save(null));
    }

    @Test
    public void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(consoleRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> consoleService.save(new ConsoleDto()));
    }

    @Test
    public void save_withNewConsoleDto_savesConsoleDto() {
        // Arrange
        Mockito.when(consoleRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(consoleRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Console());

        // Act
        consoleService.save(new ConsoleDto());

        // Assert
        Mockito.verify(consoleRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(consoleRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> consoleService.findById(0L));
    }

    @Test
    public void findById_withValidConsole_returnsConsoleDto() {
        // Arrange
        Console console = new Console();
        console.setId(1L);
        console.setName("test-name");
        console.setDescription("test-description");
        console.setReleaseDate(LocalDate.now());
        console.setVersion(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(consoleRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(console));

        // Act
        ConsoleDto result = consoleService.findById(0L);

        // Assert
        Assertions.assertEquals(console.getId(), result.getId(), "The ID does match the entity.");
        Assertions.assertEquals(console.getName(), result.getName(), "The name does match the entity.");
        Assertions.assertEquals(console.getDescription(), result.getDescription(), "The description does match the entity.");
        Assertions.assertEquals(console.getReleaseDate(), result.getReleaseDate(), "The release date does match the entity.");
        Assertions.assertEquals(console.getVersion(), result.getVersion(), "The version does match the entity.");
    }

    @Test
    public void findAll_withNoConsolesAndNoPageable_returnsEmptyList() {
        // Arrange
        Mockito.when(consoleRepository.findAll())
                .thenReturn(Collections.emptyList());

        // Act
        List<ConsoleDto> result = StreamSupport.stream(consoleService.findAll().spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "There should be no console dto's if no consoles were found.");

        Mockito.verify(consoleMapper, Mockito.never())
                .consoleToConsoleDto(ArgumentMatchers.any());
    }

    @Test
    public void findAll_withConsolesAndNoPageable_returnsListOfConsoleDtos() {
        // Arrange
        Mockito.when(consoleRepository.findAll())
                .thenReturn(Arrays.asList(new Console(), new Console()));

        // Act
        List<ConsoleDto> result = StreamSupport.stream(consoleService.findAll().spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "There should be console dto's if consoles were found.");

        Mockito.verify(consoleMapper, Mockito.atMost(2))
                .consoleToConsoleDto(ArgumentMatchers.any());
    }

    @Test
    public void findAll_withNullPageable_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> consoleService.findAll(Mockito.mock(ConsoleSpecification.class), null));
    }

    @Test
    public void findAll_withNoConsoles_returnsEmptyList() {
        // Arrange
        Mockito.when(consoleRepository.findAll(ArgumentMatchers.any(ConsoleSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        ConsoleSpecification consoleSpecification = Mockito.mock(ConsoleSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<ConsoleDto> result = StreamSupport.stream(consoleService.findAll(consoleSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no pages console results were found.");
    }

    @Test
    public void findAll_withConsoles_returnsConsolesAsConsoleDtos() {
        // Arrange
        Page<Console> consoles = new PageImpl<>(Arrays.asList(new Console(), new Console()));

        Mockito.when(consoleRepository.findAll(ArgumentMatchers.any(ConsoleSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(consoles);

        ConsoleSpecification consoleSpecification = Mockito.mock(ConsoleSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<ConsoleDto> result = StreamSupport.stream(consoleService.findAll(consoleSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result shouldn't be empty if the repository returned consoles.");
    }

    @Test
    public void update_withNullConsoleDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> consoleService.update(null));
    }

    @Test
    public void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(consoleRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> consoleService.update(new ConsoleDto()));
    }

    @Test
    public void update_withExistingConsoleDto_updatesConsoleDto() {
        // Arrange
        Mockito.when(consoleRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(consoleRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Console());

        // Act
        consoleService.update(new ConsoleDto());

        // Assert
        Mockito.verify(consoleRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void patch_withNoConsoleMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(consoleRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> consoleService.patch(0L, Mockito.mock(JsonMergePatch.class)));
    }

    @Test
    public void patch_withValidId_savesConsole() {
        // Arrange
        Mockito.when(consoleRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Console()));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(patchService.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new ConsoleDto());

        // Act
        consoleService.patch(0L, Mockito.mock(JsonMergePatch.class));

        // Assert
        Mockito.verify(consoleRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(consoleRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> consoleService.deleteById(0L));
    }

    @Test
    public void delete_withExistingId_invokesDeletion() {
        // Arrange
        Mockito.when(consoleRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.doNothing().when(consoleRepository)
                .deleteById(ArgumentMatchers.anyLong());

        // Act
        consoleService.deleteById(0L);

        // Assert
        Mockito.verify(consoleRepository, Mockito.atMostOnce())
                .deleteById(ArgumentMatchers.anyLong());
    }
}
