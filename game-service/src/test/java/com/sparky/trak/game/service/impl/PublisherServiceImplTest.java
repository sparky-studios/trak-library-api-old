package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.domain.GamePublisherXref;
import com.sparky.trak.game.domain.Publisher;
import com.sparky.trak.game.repository.GamePublisherXrefRepository;
import com.sparky.trak.game.repository.GameRepository;
import com.sparky.trak.game.repository.PublisherRepository;
import com.sparky.trak.game.repository.specification.PublisherSpecification;
import com.sparky.trak.game.service.PatchService;
import com.sparky.trak.game.service.dto.PublisherDto;
import com.sparky.trak.game.service.mapper.PublisherMapper;
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
public class PublisherServiceImplTest {

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GamePublisherXrefRepository gamePublisherXrefRepository;

    @Mock
    private MessageSource messageSource;

    @Spy
    private final PublisherMapper publisherMapper = PublisherMapper.INSTANCE;

    @Mock
    private PatchService patchService;

    @InjectMocks
    private PublisherServiceImpl publisherService;

    @Test
    public void save_withNullPublisherDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> publisherService.save(null));
    }

    @Test
    public void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> publisherService.save(new PublisherDto()));
    }

    @Test
    public void save_withNewPublisherDto_savesPublisherDto() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(publisherRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Publisher());

        // Act
        publisherService.save(new PublisherDto());

        // Assert
        Mockito.verify(publisherRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(publisherRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.findById(0L));
    }

    @Test
    public void findById_withValidPublisher_returnsPublisherDto() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setId(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(publisherRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(publisher));

        // Act
        PublisherDto result = publisherService.findById(0L);

        // Assert
        Assertions.assertNotNull(result, "The mapped result should not be null.");
    }

    @Test
    public void findPublishersByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.findPublishersByGameId(0L));
    }

    @Test
    public void findPublishersByGameId_withNoPublishers_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(gamePublisherXrefRepository.findAll(ArgumentMatchers.<Specification<GamePublisherXref>>any()))
                .thenReturn(Collections.emptyList());

        // Act
        List<PublisherDto> result = StreamSupport.stream(publisherService.findPublishersByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(publisherMapper, Mockito.never())
                .publisherToPublisherDto(ArgumentMatchers.any());
    }

    @Test
    public void findPublishersByGameId_withMultiplePublishers_returnsList() {
        // Arrange
        Mockito.when(gameRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        GamePublisherXref gamePublisherXref1 = new GamePublisherXref();
        gamePublisherXref1.setPublisher(new Publisher());

        GamePublisherXref gamePublisherXref2 = new GamePublisherXref();
        gamePublisherXref2.setPublisher(new Publisher());

        Mockito.when(gamePublisherXrefRepository.findAll(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(gamePublisherXref1, gamePublisherXref2)));

        // Act
        List<PublisherDto> result = StreamSupport.stream(publisherService.findPublishersByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");
        Assertions.assertEquals(2, result.size(), "There should be only two publishers if there are two xrefs");

        Mockito.verify(publisherMapper, Mockito.atMost(2))
                .publisherToPublisherDto(ArgumentMatchers.any());
    }

    @Test
    public void findAll_withNullPageable_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> publisherService.findAll(Mockito.mock(PublisherSpecification.class), null));
    }

    @Test
    public void findAll_withNoCompanies_returnsEmptyList() {
        // Arrange
        Mockito.when(publisherRepository.findAll(ArgumentMatchers.any(PublisherSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        PublisherSpecification publisherSpecification = Mockito.mock(PublisherSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<PublisherDto> result = StreamSupport.stream(publisherService.findAll(publisherSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no paged publisher results were found.");
    }

    @Test
    public void findAll_withCompanies_returnsCompaniesAsPublisherDtos() {
        // Arrange
        Page<Publisher> companies = new PageImpl<>(Arrays.asList(new Publisher(), new Publisher()));

        Mockito.when(publisherRepository.findAll(ArgumentMatchers.any(PublisherSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(companies);

        PublisherSpecification publisherSpecification = Mockito.mock(PublisherSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<PublisherDto> result = StreamSupport.stream(publisherService.findAll(publisherSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result shouldn't be empty if the repository returned companies.");
    }

    @Test
    public void count_withNullPublisherSpecification_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> publisherService.count(null));
    }

    @Test
    public void count_withPlatformSpecification_invokesCount() {
        // Arrange
        Mockito.when(publisherRepository.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        publisherService.count(Mockito.mock(PublisherSpecification.class));

        // Assert
        Mockito.verify(publisherRepository, Mockito.atMostOnce())
                .count(Mockito.any());
    }

    @Test
    public void update_withNullPublisherDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> publisherService.update(null));
    }

    @Test
    public void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.update(new PublisherDto()));
    }

    @Test
    public void update_withExistingPublisherDto_updatesPublisherDto() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(publisherRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Publisher());

        // Act
        publisherService.update(new PublisherDto());

        // Assert
        Mockito.verify(publisherRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void patch_withNoPublisherMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(publisherRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.patch(0L, Mockito.mock(JsonMergePatch.class)));
    }

    @Test
    public void patch_withValidId_savesPublisher() {
        // Arrange
        Mockito.when(publisherRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Publisher()));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(patchService.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new PublisherDto());

        // Act
        publisherService.patch(0L, Mockito.mock(JsonMergePatch.class));

        // Assert
        Mockito.verify(publisherRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.deleteById(0L));
    }

    @Test
    public void delete_withExistingId_invokesDeletion() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.doNothing().when(publisherRepository)
                .deleteById(ArgumentMatchers.anyLong());

        // Act
        publisherService.deleteById(0L);

        // Assert
        Mockito.verify(publisherRepository, Mockito.atMostOnce())
                .deleteById(ArgumentMatchers.anyLong());
    }
}
