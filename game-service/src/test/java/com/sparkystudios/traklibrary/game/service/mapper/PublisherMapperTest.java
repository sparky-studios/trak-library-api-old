package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Publisher;
import com.sparkystudios.traklibrary.game.service.dto.PublisherDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        PublisherMapperImpl.class,
})
class PublisherMapperTest {

    @Autowired
    private PublisherMapper publisherMapper;

    @Test
    void fromPublisher_withNull_returnsNull() {
        // Act
        PublisherDto result = publisherMapper.fromPublisher(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void fromPublisher_withPublisher_mapsFields() {
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
        PublisherDto result = publisherMapper.fromPublisher(publisher);

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
    void toPublisher_withNull_returnsNull() {
        // Act
        Publisher result = publisherMapper.toPublisher(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void toPublisher_withPublisherDto_mapsFields() {
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
        Publisher result = publisherMapper.toPublisher(publisherDto);

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
