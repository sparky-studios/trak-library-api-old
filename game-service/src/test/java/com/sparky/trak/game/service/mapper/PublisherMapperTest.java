package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.Publisher;
import com.sparky.trak.game.service.dto.PublisherDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class PublisherMapperTest {

    @Test
    public void publisherToPublisherDto_withPublisher_mapsFields() {
        // Arrange
        Publisher publisher = new Publisher();
        publisher.setId(5L);
        publisher.setName("test-name");
        publisher.setDescription("test-description");
        publisher.setFoundedDate(LocalDate.now());
        publisher.setVersion(1L);

        // Act
        PublisherDto result = PublisherMapper.INSTANCE.publisherToPublisherDto(publisher);

        // Assert
        Assertions.assertEquals(publisher.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(publisher.getName(), result.getName(), "The mapped name does not match the entity.");
        Assertions.assertEquals(publisher.getDescription(), result.getDescription(), "The mapped description does not match the entity.");
        Assertions.assertEquals(publisher.getFoundedDate(), result.getFoundedDate(), "The mapped founded date does not match the entity.");
        Assertions.assertEquals(publisher.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
    }

    @Test
    public void publisherDtoToPublisher_withPublisherDto_mapsFields() {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(5L);
        publisherDto.setName("test-name");
        publisherDto.setDescription("test-description");
        publisherDto.setFoundedDate(LocalDate.now());
        publisherDto.setVersion(1L);

        // Act
        Publisher result = PublisherMapper.INSTANCE.publisherDtoToPublisher(publisherDto);

        // Assert
        Assertions.assertEquals(publisherDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(publisherDto.getName(), result.getName(), "The mapped name does not match the DTO.");
        Assertions.assertEquals(publisherDto.getDescription(), result.getDescription(), "The mapped description does not match the DTO.");
        Assertions.assertEquals(publisherDto.getFoundedDate(), result.getFoundedDate(), "The mapped founded date does not match the DTO.");
        Assertions.assertEquals(publisherDto.getVersion(), result.getVersion(), "The mapped version does not match the DTO.");
    }
}
