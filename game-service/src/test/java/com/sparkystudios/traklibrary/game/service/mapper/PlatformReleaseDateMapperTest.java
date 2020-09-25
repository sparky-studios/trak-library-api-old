package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameRegion;
import com.sparkystudios.traklibrary.game.domain.PlatformReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.PlatformReleaseDateDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class PlatformReleaseDateMapperTest {

    @Test
    void platformReleaseDateToPlatformReleaseDateDto_withNull_returnsNull() {
        // Act
        PlatformReleaseDateDto result = GameMappers.PLATFORM_RELEASE_DATE_MAPPER.platformReleaseDateToPlatformReleaseDateDto(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    void platformReleaseDateToPlatformReleaseDateDto_withPlatformReleaseDate_mapsFields() {
        // Arrange
        PlatformReleaseDate platformReleaseDate = new PlatformReleaseDate();
        platformReleaseDate.setRegion(GameRegion.JAPAN);
        platformReleaseDate.setReleaseDate(LocalDate.now());
        platformReleaseDate.setVersion(2L);

        // Act
        PlatformReleaseDateDto result = GameMappers.PLATFORM_RELEASE_DATE_MAPPER.platformReleaseDateToPlatformReleaseDateDto(platformReleaseDate);

        // Assert
        Assertions.assertEquals(platformReleaseDate.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(platformReleaseDate.getRegion(), result.getRegion(), "The mapped region does not match the entity.");
        Assertions.assertEquals(platformReleaseDate.getReleaseDate(), result.getReleaseDate(), "The mapped release date does not match the entity.");
        Assertions.assertEquals(platformReleaseDate.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
    }

    @Test
    void platformReleaseDateDtoToPlatformReleaseDate_withNull_returnsNull() {
        // Act
        PlatformReleaseDate result = GameMappers.PLATFORM_RELEASE_DATE_MAPPER.platformReleaseDateDtoToPlatformReleaseDate(null);

        // Assert
        Assertions.assertNull(result, "The result should be null if the argument passed in is null.");
    }

    @Test
    void platformReleaseDateDtoToPlatformReleaseDate_withPlatformReleaseDateDto_mapsFields() {
        // Arrange
        PlatformReleaseDateDto platformReleaseDateDto = new PlatformReleaseDateDto();
        platformReleaseDateDto.setRegion(GameRegion.JAPAN);
        platformReleaseDateDto.setReleaseDate(LocalDate.now());
        platformReleaseDateDto.setVersion(2L);

        // Act
        PlatformReleaseDate result = GameMappers.PLATFORM_RELEASE_DATE_MAPPER.platformReleaseDateDtoToPlatformReleaseDate(platformReleaseDateDto);

        // Assert
        Assertions.assertEquals(platformReleaseDateDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(platformReleaseDateDto.getRegion(), result.getRegion(), "The mapped region does not match the DTO.");
        Assertions.assertEquals(platformReleaseDateDto.getReleaseDate(), result.getReleaseDate(), "The mapped release date does not match the DTO.");
        Assertions.assertEquals(platformReleaseDateDto.getVersion(), result.getVersion(), "The mapped version does not match the DTO.");
    }
}
