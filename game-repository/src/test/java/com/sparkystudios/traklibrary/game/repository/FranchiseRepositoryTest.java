package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.Franchise;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class FranchiseRepositoryTest {

    @Autowired
    private FranchiseRepository franchiseRepository;

    @Test
    void findBySlug_withNonExistentDeveloper_returnsEmptyOptional() {
        // Act
        Optional<Franchise> result = franchiseRepository.findBySlug("test-slug");

        // Act
        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    void findBySlug_withDeveloper_returnsDeveloper() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setTitle("test-title");
        franchise.setDescription("test-description");
        franchise.setSlug("test-slug");
        franchise = franchiseRepository.save(franchise);

        // Act
        Optional<Franchise> result = franchiseRepository.findBySlug("test-slug");

        // Assert
        Assertions.assertThat(result).isPresent()
                .isEqualTo(Optional.of(franchise));
    }
}
