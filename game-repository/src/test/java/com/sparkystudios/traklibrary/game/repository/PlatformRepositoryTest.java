package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.Platform;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class PlatformRepositoryTest {

    @Autowired
    private PlatformRepository platformRepository;

    @Test
    void findBySlug_withNonExistentPlatform_returnsEmptyOptional() {
        // Act
        Optional<Platform> result = platformRepository.findBySlug("test-slug");

        // Assert
        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    void findBySlug_withPlatform_returnsPlatform() {
        // Arrange
        Platform platform = new Platform();
        platform.setName("test-name");
        platform.setDescription("test-description");
        platform.setSlug("test-slug");
        platform = platformRepository.save(platform);

        // Act
        Optional<Platform> result = platformRepository.findBySlug("test-slug");

        // Assert
        Assertions.assertThat(result).isPresent()
                .isEqualTo(Optional.of(platform));
    }
}
