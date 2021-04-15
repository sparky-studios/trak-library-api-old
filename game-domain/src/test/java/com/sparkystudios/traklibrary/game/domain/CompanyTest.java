package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.Collections;

@DataJpaTest
class CompanyTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullName_throwsPersistenceException() {
        // Arrange
        Company company = new Company();
        company.setName(null);
        company.setDescription("test-description");
        company.setFoundedDate(LocalDate.now());
        company.setSlug("test-slug");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(company));
    }

    @Test
    void persist_withNameExceedingLength_throwsPersistenceException() {
        // Arrange
        Company company = new Company();
        company.setName(String.join("", Collections.nCopies(300, "t")));
        company.setDescription("test-description");
        company.setFoundedDate(LocalDate.now());
        company.setSlug("test-slug");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(company));
    }

    @Test
    void persist_withDescriptionExceedingLength_throwsPersistenceException() {
        // Arrange
        Company company = new Company();
        company.setName("test-name");
        company.setDescription(String.join("", Collections.nCopies(5000, "t")));
        company.setFoundedDate(LocalDate.now());
        company.setSlug("test-slug");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(company));
    }

    @Test
    void persist_withNullFoundedDate_throwsPersistenceException() {
        // Arrange
        Company company = new Company();
        company.setName("test-name");
        company.setDescription("test-description");
        company.setFoundedDate(null);
        company.setSlug("test-slug");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(company));
    }

    @Test
    void persist_withNullSlug_throwsPersistenceException() {
        // Arrange
        Company company = new Company();
        company.setName("test-name");
        company.setDescription("test-description");
        company.setFoundedDate(LocalDate.now());
        company.setSlug(null);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(company));
    }

    @Test
    void persist_withSlugExceedingLength_throwsPersistenceException() {
        // Arrange
        Company company = new Company();
        company.setName("test-name");
        company.setDescription("test-description");
        company.setFoundedDate(LocalDate.now());
        company.setSlug(String.join("", Collections.nCopies(5000, "t")));

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(company));
    }

    @Test
    void persist_withValidCompany_mapsCompany() {
        // Arrange
        Company company = new Company();
        company.setName("test-name");
        company.setDescription("test-description");
        company.setFoundedDate(LocalDate.now());
        company.setSlug("test-slug");

        // Act
        Company result = testEntityManager.persistFlushFind(company);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getName()).isEqualTo(company.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(company.getDescription());
        Assertions.assertThat(result.getFoundedDate()).isEqualTo(company.getFoundedDate());
        Assertions.assertThat(result.getSlug()).isEqualTo(company.getSlug());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }
}
