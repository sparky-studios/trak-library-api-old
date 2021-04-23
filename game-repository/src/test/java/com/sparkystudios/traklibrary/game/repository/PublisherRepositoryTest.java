package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.Publisher;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
class PublisherRepositoryTest {

    @Autowired
    private PublisherRepository publisherRepository;

    @Test
    void findBySlug_withNonExistentPublisher_returnsEmptyOptional() {
        // Act
        Optional<Publisher> result = publisherRepository.findBySlug("test-slug");

        // Act
        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    void findBySlug_withPublisher_returnsPublisher() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setName("test-name");
        publisher.setDescription("test-description");
        publisher.setFoundedDate(LocalDate.now());
        publisher.setSlug("test-slug");
        publisher = publisherRepository.save(publisher);

        // Act
        Optional<Publisher> result = publisherRepository.findBySlug("test-slug");

        // Assert
        Assertions.assertThat(result).isPresent()
                .isEqualTo(Optional.of(publisher));
    }
}
