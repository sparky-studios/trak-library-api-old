package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.Developer;
import com.traklibrary.game.service.dto.DeveloperDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class DeveloperMapperTest {

    @Test
    void developerToDeveloperDto_withNull_returnsNull() {
        // Act
        DeveloperDto result = DeveloperMapper.INSTANCE.developerToDeveloperDto(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    void developerToDeveloperDto_withDeveloper_mapsFields() {
        // Arrange
        Developer developer = new Developer();
        developer.setId(5L);
        developer.setName("test-name");
        developer.setDescription("test-description");
        developer.setFoundedDate(LocalDate.now());
        developer.setVersion(1L);

        // Act
        DeveloperDto result = DeveloperMapper.INSTANCE.developerToDeveloperDto(developer);

        // Assert
        Assertions.assertEquals(developer.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(developer.getName(), result.getName(), "The mapped name does not match the entity.");
        Assertions.assertEquals(developer.getDescription(), result.getDescription(), "The mapped description does not match the entity.");
        Assertions.assertEquals(developer.getFoundedDate(), result.getFoundedDate(), "The mapped founded date does not match the entity.");
        Assertions.assertEquals(developer.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
    }

    @Test
    void developerDtoToDeveloper_withNull_returnsNull() {
        // Act
        Developer result = DeveloperMapper.INSTANCE.developerDtoToDeveloper(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    void developerDtoToDeveloper_withDeveloperDto_mapsFields() {
        // Arrange
        DeveloperDto developerDto = new DeveloperDto();
        developerDto.setId(5L);
        developerDto.setName("test-name");
        developerDto.setDescription("test-description");
        developerDto.setFoundedDate(LocalDate.now());
        developerDto.setVersion(1L);

        // Act
        Developer result = DeveloperMapper.INSTANCE.developerDtoToDeveloper(developerDto);

        // Assert
        Assertions.assertEquals(developerDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(developerDto.getName(), result.getName(), "The mapped name does not match the DTO.");
        Assertions.assertEquals(developerDto.getDescription(), result.getDescription(), "The mapped description does not match the DTO.");
        Assertions.assertEquals(developerDto.getFoundedDate(), result.getFoundedDate(), "The mapped founded date does not match the DTO.");
        Assertions.assertEquals(developerDto.getVersion(), result.getVersion(), "The mapped version does not match the DTO.");
    }
}
