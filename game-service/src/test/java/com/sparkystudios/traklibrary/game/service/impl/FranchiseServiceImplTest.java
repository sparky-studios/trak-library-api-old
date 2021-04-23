package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.Franchise;
import com.sparkystudios.traklibrary.game.repository.FranchiseRepository;
import com.sparkystudios.traklibrary.game.repository.specification.FranchiseSpecification;
import com.sparkystudios.traklibrary.game.service.PatchService;
import com.sparkystudios.traklibrary.game.service.dto.FranchiseDto;
import com.sparkystudios.traklibrary.game.service.mapper.FranchiseMapper;
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
class FranchiseServiceImplTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private FranchiseMapper franchiseMapper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private PatchService patchService;

    @InjectMocks
    private FranchiseServiceImpl franchiseService;

    @Test
    void save_withNullFranchiseDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> franchiseService.save(null));
    }

    @Test
    void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(franchiseRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        FranchiseDto franchiseDto = new FranchiseDto();

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> franchiseService.save(franchiseDto));
    }

    @Test
    void save_withNewFranchiseDto_savesFranchiseDto() {
        // Arrange
        Mockito.when(franchiseRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(franchiseRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Franchise());

        // Act
        franchiseService.save(new FranchiseDto());

        // Assert
        Mockito.verify(franchiseRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(franchiseMapper, Mockito.atMostOnce())
                .fromFranchise(ArgumentMatchers.any());
    }

    @Test
    void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(franchiseRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> franchiseService.findById(0L));
    }

    @Test
    void findById_withValidFranchise_returnsFranchiseDto() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setId(1L);
        franchise.setTitle("test-title");
        franchise.setDescription("test-description");
        franchise.setVersion(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(franchiseRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(franchise));

        Mockito.when(franchiseMapper.fromFranchise(ArgumentMatchers.any()))
                .thenReturn(new FranchiseDto());

        // Act
        FranchiseDto result = franchiseService.findById(0L);

        // Assert
        Assertions.assertNotNull(result, "The mapped result should not be null.");

        Mockito.verify(franchiseMapper, Mockito.atMostOnce())
                .fromFranchise(ArgumentMatchers.any());
    }

    @Test
    void findBySlug_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(franchiseRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> franchiseService.findBySlug("slug"));
    }

    @Test
    void findBySlug_withValidFranchise_returnsFranchiseDto() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setId(1L);
        franchise.setTitle("test-title");
        franchise.setDescription("test-description");
        franchise.setVersion(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(franchiseRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(franchise));

        Mockito.when(franchiseMapper.fromFranchise(ArgumentMatchers.any()))
                .thenReturn(new FranchiseDto());

        // Act
        FranchiseDto result = franchiseService.findBySlug("slug");

        // Assert
        Assertions.assertNotNull(result, "The mapped result should not be null.");

        Mockito.verify(franchiseMapper, Mockito.atMostOnce())
                .fromFranchise(ArgumentMatchers.any());
    }

    @Test
    void findAll_withNullPageable_throwsNullPointerException() {
        // Arrange
        FranchiseSpecification franchiseSpecification = Mockito.mock(FranchiseSpecification.class);

        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> franchiseService.findAll(franchiseSpecification, null));
    }

    @Test
    void findAll_withNoFranchises_returnsEmptyList() {
        // Arrange
        Mockito.when(franchiseRepository.findAll(ArgumentMatchers.any(FranchiseSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        FranchiseSpecification franchiseSpecification = Mockito.mock(FranchiseSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<FranchiseDto> result = StreamSupport.stream(franchiseService.findAll(franchiseSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no paged franchise results were found.");

        Mockito.verify(franchiseMapper, Mockito.never())
                .fromFranchise(ArgumentMatchers.any());
    }

    @Test
    void findAll_withFranchises_returnsFranchisesAsFranchiseDtos() {
        // Arrange
        Page<Franchise> franchises = new PageImpl<>(Arrays.asList(new Franchise(), new Franchise()));

        Mockito.when(franchiseRepository.findAll(ArgumentMatchers.any(FranchiseSpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(franchises);

        FranchiseSpecification franchiseSpecification = Mockito.mock(FranchiseSpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<FranchiseDto> result = StreamSupport.stream(franchiseService.findAll(franchiseSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result shouldn't be empty if the repository returned franchises.");

        Mockito.verify(franchiseMapper, Mockito.atMost(2))
                .fromFranchise(ArgumentMatchers.any());
    }

    @Test
    void count_withNullFranchiseSpecification_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> franchiseService.count(null));
    }

    @Test
    void count_withValidGameUserEntrySpecification_invokesCount() {
        // Arrange
        Mockito.when(franchiseRepository.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        franchiseService.count(Mockito.mock(FranchiseSpecification.class));

        // Assert
        Mockito.verify(franchiseRepository, Mockito.atMostOnce())
                .count(Mockito.any());
    }

    @Test
    void update_withNullFranchiseDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> franchiseService.update(null));
    }

    @Test
    void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(franchiseRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        FranchiseDto franchiseDto = new FranchiseDto();

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> franchiseService.update(franchiseDto));
    }

    @Test
    void update_withExistingFranchiseDto_updatesFranchiseDto() {
        // Arrange
        Mockito.when(franchiseRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(franchiseRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Franchise());

        // Act
        franchiseService.update(new FranchiseDto());

        // Assert
        Mockito.verify(franchiseRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(franchiseMapper, Mockito.atMostOnce())
                .fromFranchise(ArgumentMatchers.any());
    }

    @Test
    void patch_withNoFranchiseMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(franchiseRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        JsonMergePatch jsonMergePatch = Mockito.mock(JsonMergePatch.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> franchiseService.patch(0L, jsonMergePatch));
    }

    @Test
    void patch_withValidId_saveFranchise() {
        // Arrange
        Mockito.when(franchiseRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Franchise()));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(patchService.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new FranchiseDto());

        // Act
        franchiseService.patch(0L, Mockito.mock(JsonMergePatch.class));

        // Assert
        Mockito.verify(franchiseRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(franchiseMapper, Mockito.atMost(2))
                .fromFranchise(ArgumentMatchers.any());
    }

    @Test
    void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(franchiseRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> franchiseService.deleteById(0L));
    }

    @Test
    void delete_withExistingId_invokesDeletion() {
        // Arrange
        Mockito.when(franchiseRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.doNothing().when(franchiseRepository)
                .deleteById(ArgumentMatchers.anyLong());

        // Act
        franchiseService.deleteById(0L);

        // Assert
        Mockito.verify(franchiseRepository, Mockito.atMostOnce())
                .deleteById(ArgumentMatchers.anyLong());
    }
}
