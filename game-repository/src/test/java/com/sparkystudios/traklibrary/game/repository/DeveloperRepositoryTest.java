package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.Developer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
class DeveloperRepositoryTest {

    @Autowired
    private DeveloperRepository developerRepository;

    @Test
    void findBySlug_withNonExistentDeveloper_returnsEmptyOptional() {
        // Act
        Optional<Developer> result = developerRepository.findBySlug("test-slug");

        // Act
        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    void findBySlug_withDeveloper_returnsDeveloper() {
        // Arrange
        Developer developer = new Developer();
        developer.setName("test-name");
        developer.setDescription("test-description");
        developer.setFoundedDate(LocalDate.now());
        developer.setSlug("test-slug");
        developer = developerRepository.save(developer);

        // Act
        Optional<Developer> result = developerRepository.findBySlug("test-slug");

        // Assert
        Assertions.assertThat(result).isPresent()
                .isEqualTo(Optional.of(developer));
    }
}
