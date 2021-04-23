package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.Publisher;
import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.repository.PublisherRepository;
import com.sparkystudios.traklibrary.game.repository.specification.PublisherSpecification;
import com.sparkystudios.traklibrary.game.service.PatchService;
import com.sparkystudios.traklibrary.game.service.dto.PublisherDto;
import com.sparkystudios.traklibrary.game.service.mapper.PublisherMapper;
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
class PublisherServiceImplTest {

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private PublisherMapper publisherMapper;

    @Mock
    private PatchService patchService;

    @InjectMocks
    private PublisherServiceImpl publisherService;

    @Test
    void save_withNullPublisherDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> publisherService.save(null));
    }

    @Test
    void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        PublisherDto publisherDto = new PublisherDto();

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> publisherService.save(publisherDto));
    }

    @Test
    void save_withNewPublisherDto_savesPublisherDto() {
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

        Mockito.verify(publisherMapper, Mockito.atMostOnce())
                .fromPublisher(ArgumentMatchers.any());
    }

    @Test
    void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(publisherRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.findById(0L));
    }

    @Test
    void findById_withValidPublisher_returnsPublisherDto() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setId(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(publisherRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(publisher));

        Mockito.when(publisherMapper.fromPublisher(ArgumentMatchers.any()))
                .thenReturn(new PublisherDto());

        // Act
        PublisherDto result = publisherService.findById(0L);

        // Assert
        Assertions.assertNotNull(result, "The mapped result should not be null.");

        Mockito.verify(publisherMapper, Mockito.atMostOnce())
                .fromPublisher(ArgumentMatchers.any());
    }

    @Test
    void findBySlug_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(publisherRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.findBySlug("slug"));
    }

    @Test
    void findBySlug_withValidPublisher_returnsPublisherDto() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setId(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(publisherRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(publisher));

        Mockito.when(publisherMapper.fromPublisher(ArgumentMatchers.any()))
                .thenReturn(new PublisherDto());

        // Act
        PublisherDto result = publisherService.findBySlug("slug");

        // Assert
        Assertions.assertNotNull(result, "The mapped result should not be null.");

        Mockito.verify(publisherMapper, Mockito.atMostOnce())
                .fromPublisher(ArgumentMatchers.any());
    }

    @Test
    void findPublishersByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.findPublishersByGameId(0L));
    }

    @Test
    void findPublishersByGameId_withNoPublishers_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Game()));

        // Act
        List<PublisherDto> result = StreamSupport.stream(publisherService.findPublishersByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(publisherMapper, Mockito.never())
                .fromPublisher(ArgumentMatchers.any());
    }

    @Test
    void findPublishersByGameId_withMultiplePublishers_returnsList() {
        // Arrange
        Publisher publisher1 = new Publisher();
        publisher1.setName("publisher-1");

        Publisher publisher2 = new Publisher();
        publisher2.setName("publisher-2");

        Game game = new Game();
        game.addPublisher(publisher1);
        game.addPublisher(publisher2);

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        // Act
        List<PublisherDto> result = StreamSupport.stream(publisherService.findPublishersByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if games are returned.");
        Assertions.assertEquals(2, result.size(), "There should be only two publishers if there are two xrefs");

        Mockito.verify(publisherMapper, Mockito.atMost(2))
                .fromPublisher(ArgumentMatchers.any());
    }

    @Test
    void findAll_withNullPageable_throwsNullPointerException() {
        // Arrange
        PublisherSpecification publisherSpecification = Mockito.mock(PublisherSpecification.class);

        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> publisherService.findAll(publisherSpecification, null));
    }

    @Test
    void findAll_withNoCompanies_returnsEmptyList() {
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

        Mockito.verify(publisherMapper, Mockito.never())
                .fromPublisher(ArgumentMatchers.any());
    }

    @Test
    void findAll_withCompanies_returnsCompaniesAsPublisherDtos() {
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

        Mockito.verify(publisherMapper, Mockito.atMost(2))
                .fromPublisher(ArgumentMatchers.any());
    }

    @Test
    void count_withNullPublisherSpecification_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> publisherService.count(null));
    }

    @Test
    void count_withPlatformSpecification_invokesCount() {
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
    void update_withNullPublisherDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> publisherService.update(null));
    }

    @Test
    void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        PublisherDto publisherDto = new PublisherDto();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.update(publisherDto));
    }

    @Test
    void update_withExistingPublisherDto_updatesPublisherDto() {
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

        Mockito.verify(publisherMapper, Mockito.atMostOnce())
                .fromPublisher(ArgumentMatchers.any());
    }

    @Test
    void patch_withNoPublisherMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(publisherRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        JsonMergePatch jsonMergePatch = Mockito.mock(JsonMergePatch.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.patch(0L, jsonMergePatch));
    }

    @Test
    void patch_withValidId_savesPublisher() {
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

        Mockito.verify(publisherMapper, Mockito.atMost(2))
                .fromPublisher(ArgumentMatchers.any());
    }

    @Test
    void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(publisherRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.deleteById(0L));
    }

    @Test
    void delete_withExistingId_invokesDeletion() {
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
