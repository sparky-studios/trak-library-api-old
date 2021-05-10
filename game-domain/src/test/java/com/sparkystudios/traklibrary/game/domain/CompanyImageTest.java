package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDate;

@DataJpaTest
class CompanyImageTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullCompany_throwsPersistenceException() {
        // Arrange
        CompanyImage companyImage = new CompanyImage();
        companyImage.setFilename("filename.png");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(companyImage));
    }

    @Test
    void persist_withValidCompany_mapsCompanyImage() {
        // Arrange
        Company company = new Company();
        company.setName("test-company");
        company.setDescription("test-description");
        company.setFoundedDate(LocalDate.now());
        company.setSlug("test-slug");
        company = testEntityManager.persistFlushFind(company);

        CompanyImage companyImage = new CompanyImage();
        companyImage.setFilename("filename.png");
        companyImage.setCompanyId(company.getId());

        // Act
        CompanyImage result = testEntityManager.persistFlushFind(companyImage);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getCompanyId()).isEqualTo(company.getId());
        Assertions.assertThat(result.getCompany().getId())
                .isEqualTo(result.getCompany().getId());
        Assertions.assertThat(result.getFilename()).isEqualTo(companyImage.getFilename());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }

    @Test
    void persist_withMultipleImagesForSameCompany_throwsPersistenceException() {
        // Arrange
        Company company = new Company();
        company.setName("test-company");
        company.setDescription("test-description");
        company.setFoundedDate(LocalDate.now());
        company.setSlug("test-slug");
        company = testEntityManager.persistFlushFind(company);

        CompanyImage companyImage1 = new CompanyImage();
        companyImage1.setFilename("filename.png");
        companyImage1.setCompanyId(company.getId());

        CompanyImage companyImage2 = new CompanyImage();
        companyImage2.setFilename("filename2.png");
        companyImage2.setCompanyId(company.getId());

        // Act
        testEntityManager.persistFlushFind(companyImage1);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(companyImage2));
    }
}
