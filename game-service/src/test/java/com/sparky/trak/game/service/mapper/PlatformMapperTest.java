package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.Platform;
import com.sparky.trak.game.service.dto.PlatformDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class PlatformMapperTest {

    @Test
    public void platformToPlatformDto_withNull_returnsNull() {
        // Act
        PlatformDto result = PlatformMapper.INSTANCE.platformToPlatformDto(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    public void platformToPlatformDto_withPlatform_mapsFields() {
        // Arrange
        Platform platform = new Platform();
        platform.setId(5L);
        platform.setName("test-name");
        platform.setDescription("test-description");
        platform.setReleaseDate(LocalDate.now());
        platform.setVersion(1L);

        // Act
        PlatformDto result = PlatformMapper.INSTANCE.platformToPlatformDto(platform);

        // Assert
        Assertions.assertEquals(platform.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(platform.getName(), result.getName(), "The mapped title does not match the entity.");
        Assertions.assertEquals(platform.getDescription(), result.getDescription(), "The mapped description does not match the entity.");
        Assertions.assertEquals(platform.getReleaseDate(), result.getReleaseDate(), "The mapped release date does not match the entity.");
        Assertions.assertEquals(platform.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
    }

    @Test
    public void platformDtoToPlatform_withNull_returnsNull() {
        // Act
        Platform result = PlatformMapper.INSTANCE.platformDtoToPlatform(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    public void platformToPlatformDto_withPlatformDto_mapsFields() {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(5L);
        platformDto.setName("test-name");
        platformDto.setDescription("test-description");
        platformDto.setReleaseDate(LocalDate.now());
        platformDto.setVersion(1L);

        // Act
        Platform result = PlatformMapper.INSTANCE.platformDtoToPlatform(platformDto);

        // Assert
        Assertions.assertEquals(platformDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(platformDto.getName(), result.getName(), "The mapped title does not match the DTO.");
        Assertions.assertEquals(platformDto.getDescription(), result.getDescription(), "The mapped description does not match the DTO.");
        Assertions.assertEquals(platformDto.getReleaseDate(), result.getReleaseDate(), "The mapped release date does not match the DTO.");
        Assertions.assertEquals(platformDto.getVersion(), result.getVersion(), "The mapped version does not match the DTO.");
    }
}
