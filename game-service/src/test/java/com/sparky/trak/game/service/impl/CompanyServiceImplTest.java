package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.domain.Company;
import com.sparky.trak.game.repository.CompanyRepository;
import com.sparky.trak.game.repository.specification.CompanySpecification;
import com.sparky.trak.game.service.PatchService;
import com.sparky.trak.game.service.dto.CompanyDto;
import com.sparky.trak.game.service.mapper.CompanyMapper;
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
public class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private MessageSource messageSource;

    @Spy
    private CompanyMapper companyMapper = CompanyMapper.INSTANCE;

    @Mock
    private PatchService patchService;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Test
    public void save_withNullCompanyDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> companyService.save(null));
    }

    @Test
    public void save_withExistingEntity_throwsEntityExistsException() {
        // Arrange
        Mockito.when(companyRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> companyService.save(new CompanyDto()));
    }

    @Test
    public void save_withNewCompanyDto_savesCompanyDto() {
        // Arrange
        Mockito.when(companyRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(companyRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Company());

        // Act
        companyService.save(new CompanyDto());

        // Assert
        Mockito.verify(companyRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(companyRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> companyService.findById(0L));
    }

    @Test
    public void findById_withValidCompany_returnsCompanyDto() {
        // Arrange
        Company company = new Company();
        company.setId(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(companyRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(company));

        // Act
        CompanyDto result = companyService.findById(0L);

        // Assert
        Assertions.assertNotNull(result, "The mapped result should not be null.");
    }

    @Test
    public void findAll_withNullPageable_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> companyService.findAll(Mockito.mock(CompanySpecification.class), null));
    }

    @Test
    public void findAll_withNoCompanies_returnsEmptyList() {
        // Arrange
        Mockito.when(companyRepository.findAll(ArgumentMatchers.any(CompanySpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());

        CompanySpecification companySpecification = Mockito.mock(CompanySpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<CompanyDto> result = StreamSupport.stream(companyService.findAll(companySpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no paged company results were found.");
    }

    @Test
    public void findAll_withCompanies_returnsCompaniesAsCompanyDtos() {
        // Arrange
        Page<Company> companies = new PageImpl<>(Arrays.asList(new Company(), new Company()));

        Mockito.when(companyRepository.findAll(ArgumentMatchers.any(CompanySpecification.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(companies);

        CompanySpecification companySpecification = Mockito.mock(CompanySpecification.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        // Act
        List<CompanyDto> result = StreamSupport.stream(companyService.findAll(companySpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result shouldn't be empty if the repository returned companies.");
    }

    @Test
    public void update_withNullCompanyDto_throwsNullPointerException() {
        // Assert
        Assertions.assertThrows(NullPointerException.class, () -> companyService.update(null));
    }

    @Test
    public void update_withNonExistentEntity_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(companyRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> companyService.update(new CompanyDto()));
    }

    @Test
    public void update_withExistingCompanyDto_updatesCompanyDto() {
        // Arrange
        Mockito.when(companyRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(companyRepository.save(ArgumentMatchers.any()))
                .thenReturn(new Company());

        // Act
        companyService.update(new CompanyDto());

        // Assert
        Mockito.verify(companyRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void patch_withNoCompanyMatchingId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(companyRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> companyService.patch(0L, Mockito.mock(JsonMergePatch.class)));
    }

    @Test
    public void patch_withValidId_savesCompany() {
        // Arrange
        Mockito.when(companyRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Company()));

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(patchService.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new CompanyDto());

        // Act
        companyService.patch(0L, Mockito.mock(JsonMergePatch.class));

        // Assert
        Mockito.verify(companyRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void delete_withNonExistentId_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(companyRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> companyService.deleteById(0L));
    }

    @Test
    public void delete_withExistingId_invokesDeletion() {
        // Arrange
        Mockito.when(companyRepository.existsById(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.doNothing().when(companyRepository)
                .deleteById(ArgumentMatchers.anyLong());

        // Act
        companyService.deleteById(0L);

        // Assert
        Mockito.verify(companyRepository, Mockito.atMostOnce())
                .deleteById(ArgumentMatchers.anyLong());
    }
}
