package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameRegion;
import com.sparkystudios.traklibrary.game.domain.Platform;
import com.sparkystudios.traklibrary.game.domain.PlatformReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.PlatformDto;
import com.sparkystudios.traklibrary.game.service.dto.PlatformReleaseDateDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class PlatformMapperTest {

    @Test
    void platformToPlatformDto_withNull_returnsNull() {
        // Act
        PlatformDto result = GameMappers.PLATFORM_MAPPER.platformToPlatformDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void platformToPlatformDto_withPlatform_mapsFields() {
        // Arrange
        PlatformReleaseDate platformReleaseDate = new PlatformReleaseDate();
        platformReleaseDate.setRegion(GameRegion.PAL);
        platformReleaseDate.setReleaseDate(LocalDate.now());
        platformReleaseDate.setVersion(1L);

        Platform platform = new Platform();
        platform.setId(5L);
        platform.setName("test-name");
        platform.setDescription("test-description");
        platform.setVersion(1L);
        platform.addReleaseDate(platformReleaseDate);

        // Act
        PlatformDto result = GameMappers.PLATFORM_MAPPER.platformToPlatformDto(platform);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(platform.getId());
        Assertions.assertThat(result.getName()).isEqualTo(platform.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(platform.getDescription());
        Assertions.assertThat(result.getVersion()).isEqualTo(platform.getVersion());
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
    }

    @Test
    void platformDtoToPlatform_withNull_returnsNull() {
        // Act
        Platform result = GameMappers.PLATFORM_MAPPER.platformDtoToPlatform(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void platformToPlatformDto_withPlatformDto_mapsFields() {
        // Arrange
        PlatformReleaseDateDto platformReleaseDateDto = new PlatformReleaseDateDto();
        platformReleaseDateDto.setRegion(GameRegion.PAL);
        platformReleaseDateDto.setReleaseDate(LocalDate.now());
        platformReleaseDateDto.setVersion(1L);

        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(5L);
        platformDto.setName("test-name");
        platformDto.setDescription("test-description");
        platformDto.setVersion(1L);
        platformDto.getReleaseDates().add(platformReleaseDateDto);

        // Act
        Platform result = GameMappers.PLATFORM_MAPPER.platformDtoToPlatform(platformDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(platformDto.getId());
        Assertions.assertThat(result.getName()).isEqualTo(platformDto.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(platformDto.getDescription());
        Assertions.assertThat(result.getVersion()).isEqualTo(platformDto.getVersion());
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
    }
}
