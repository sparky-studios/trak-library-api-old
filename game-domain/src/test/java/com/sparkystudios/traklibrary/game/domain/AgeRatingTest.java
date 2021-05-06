package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;

@DataJpaTest
class AgeRatingTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullAgeRatingClassification_throwsPersistenceException() {
        // Arrange
        AgeRating ageRating = new AgeRating();
        ageRating.setClassification(null);
        ageRating.setRating((short)4);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(ageRating));
    }

    @Test
    void persist_withValidAgeRating_mapsAgeRating() {
        // Arrange
        AgeRating ageRating = new AgeRating();
        ageRating.setClassification(AgeRatingClassification.CERO);
        ageRating.setRating((short)3);

        // Act
        AgeRating result = testEntityManager.persistFlushFind(ageRating);

        // Assert
        Assertions.assertThat(result.getId()).isPositive();
        Assertions.assertThat(result.getClassification()).isEqualTo(ageRating.getClassification());
        Assertions.assertThat(result.getRating()).isEqualTo(ageRating.getRating());
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        Assertions.assertThat(result.getUpdatedAt()).isNotNull();
        Assertions.assertThat(result.getVersion()).isNotNull().isNotNegative();
    }
}
