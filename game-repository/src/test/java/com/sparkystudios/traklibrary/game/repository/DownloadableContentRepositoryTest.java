package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.DownloadableContent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
class DownloadableContentRepositoryTest {

    @Autowired
    private DownloadableContentRepository downloadableContentRepository;

    @Test
    void findBySlug_withNonExistentDownloadableContent_returnsEmptyOptional() {
        // Act
        Optional<DownloadableContent> result = downloadableContentRepository.findBySlug("test-slug");

        // Arrange
        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    void findBySlug_withDownloadableContent_returnsDownloadableContent() {
        // Arrange
        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName("test-name");
        downloadableContent.setDescription("test-description");
        downloadableContent.setReleaseDate(LocalDate.now());
        downloadableContent.setSlug("test-slug");
        downloadableContent = downloadableContentRepository.save(downloadableContent);

        // Act
        Optional<DownloadableContent> result = downloadableContentRepository.findBySlug("test-slug");

        // Assert
        Assertions.assertThat(result).isPresent()
                .isEqualTo(Optional.of(downloadableContent));
    }
}
