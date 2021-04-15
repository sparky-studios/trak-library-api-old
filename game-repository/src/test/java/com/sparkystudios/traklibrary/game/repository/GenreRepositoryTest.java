package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.Genre;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void findBySlug_withNonExistentGenre_returnsEmptyOptional() {
        // Act
        Optional<Genre> result = genreRepository.findBySlug("test-slug");

        // Assert
        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    void findBySlug_withGenre_returnsGenre() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("test-name");
        genre.setDescription("test-description");
        genre.setSlug("test-slug");
        genre = genreRepository.save(genre);

        // Act
        Optional<Genre> result = genreRepository.findBySlug("test-slug");

        // Assert
        Assertions.assertThat(result).isPresent()
                .isEqualTo(Optional.of(genre));
    }
}
