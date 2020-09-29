package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Publisher;
import com.sparkystudios.traklibrary.game.service.dto.PublisherDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

class PublisherMapperTest {

    @Test
    void publisherToPublisherDto_withNull_returnsNull() {
        // Act
        PublisherDto result = GameMappers.PUBLISHER_MAPPER.publisherToPublisherDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void publisherToPublisherDto_withPublisher_mapsFields() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setId(5L);
        publisher.setName("test-name");
        publisher.setDescription("test-description");
        publisher.setFoundedDate(LocalDate.now());
        publisher.setCreatedAt(LocalDateTime.now());
        publisher.setUpdatedAt(LocalDateTime.now());
        publisher.setVersion(1L);

        // Act
        PublisherDto result = GameMappers.PUBLISHER_MAPPER.publisherToPublisherDto(publisher);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(publisher.getId());
        Assertions.assertThat(result.getName()).isEqualTo(publisher.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(publisher.getDescription());
        Assertions.assertThat(result.getFoundedDate()).isEqualTo(publisher.getFoundedDate());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(publisher.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(publisher.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(publisher.getVersion());
    }

    @Test
    void publisherDtoToPublisher_withNull_returnsNull() {
        // Act
        Publisher result = GameMappers.PUBLISHER_MAPPER.publisherDtoToPublisher(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void publisherDtoToPublisher_withPublisherDto_mapsFields() {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(5L);
        publisherDto.setName("test-name");
        publisherDto.setDescription("test-description");
        publisherDto.setFoundedDate(LocalDate.now());
        publisherDto.setCreatedAt(LocalDateTime.now());
        publisherDto.setUpdatedAt(LocalDateTime.now());
        publisherDto.setVersion(1L);

        // Act
        Publisher result = GameMappers.PUBLISHER_MAPPER.publisherDtoToPublisher(publisherDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(publisherDto.getId());
        Assertions.assertThat(result.getName()).isEqualTo(publisherDto.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(publisherDto.getDescription());
        Assertions.assertThat(result.getFoundedDate()).isEqualTo(publisherDto.getFoundedDate());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(publisherDto.getVersion());
    }
}
