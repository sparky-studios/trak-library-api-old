package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.Company;
import com.sparky.trak.game.service.dto.CompanyDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class CompanyMapperTest {

    @Test
    public void companyToCompanyDto_withNull_returnsNull() {
        // Act
        CompanyDto result = CompanyMapper.INSTANCE.companyToCompanyDto(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    public void companyToCompanyDto_withCompany_mapsFields() {
        // Arrange
        Company company = new Company();
        company.setId(5L);
        company.setName("test-name");
        company.setDescription("test-description");
        company.setFoundedDate(LocalDate.now());
        company.setVersion(1L);

        // Act
        CompanyDto result = CompanyMapper.INSTANCE.companyToCompanyDto(company);

        // Assert
        Assertions.assertEquals(company.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(company.getName(), result.getName(), "The mapped name does not match the entity.");
        Assertions.assertEquals(company.getDescription(), result.getDescription(), "The mapped description does not match the entity.");
        Assertions.assertEquals(company.getFoundedDate(), result.getFoundedDate(), "The mapped founded date does not match the entity.");
        Assertions.assertEquals(company.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
    }

    @Test
    public void companyDtoToCompany_withNull_returnsNull() {
        // Act
        Company result = CompanyMapper.INSTANCE.companyDtoToCompany(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    public void companyDtoToCompany_withCompanyDto_mapsFields() {
        // Arrange
        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(5L);
        companyDto.setName("test-name");
        companyDto.setDescription("test-description");
        companyDto.setFoundedDate(LocalDate.now());
        companyDto.setVersion(1L);

        // Act
        Company result = CompanyMapper.INSTANCE.companyDtoToCompany(companyDto);

        // Assert
        Assertions.assertEquals(companyDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(companyDto.getName(), result.getName(), "The mapped name does not match the DTO.");
        Assertions.assertEquals(companyDto.getDescription(), result.getDescription(), "The mapped description does not match the DTO.");
        Assertions.assertEquals(companyDto.getFoundedDate(), result.getFoundedDate(), "The mapped founded date does not match the DTO.");
        Assertions.assertEquals(companyDto.getVersion(), result.getVersion(), "The mapped version does not match the DTO.");
    }
}
