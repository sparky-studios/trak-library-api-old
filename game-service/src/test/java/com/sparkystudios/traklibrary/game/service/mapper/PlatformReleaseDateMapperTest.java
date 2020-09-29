package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameRegion;
import com.sparkystudios.traklibrary.game.domain.PlatformReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.PlatformReleaseDateDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

class PlatformReleaseDateMapperTest {

    @Test
    void platformReleaseDateToPlatformReleaseDateDto_withNull_returnsNull() {
        // Act
        PlatformReleaseDateDto result = GameMappers.PLATFORM_RELEASE_DATE_MAPPER.platformReleaseDateToPlatformReleaseDateDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void platformReleaseDateToPlatformReleaseDateDto_withPlatformReleaseDate_mapsFields() {
        // Arrange
        PlatformReleaseDate platformReleaseDate = new PlatformReleaseDate();
        platformReleaseDate.setRegion(GameRegion.JAPAN);
        platformReleaseDate.setReleaseDate(LocalDate.now());
        platformReleaseDate.setCreatedAt(LocalDateTime.now());
        platformReleaseDate.setUpdatedAt(LocalDateTime.now());
        platformReleaseDate.setVersion(2L);

        // Act
        PlatformReleaseDateDto result = GameMappers.PLATFORM_RELEASE_DATE_MAPPER.platformReleaseDateToPlatformReleaseDateDto(platformReleaseDate);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(platformReleaseDate.getId());
        Assertions.assertThat(result.getRegion()).isEqualTo(platformReleaseDate.getRegion());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(platformReleaseDate.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(platformReleaseDate.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(platformReleaseDate.getVersion());
    }

    @Test
    void platformReleaseDateDtoToPlatformReleaseDate_withNull_returnsNull() {
        // Act
        PlatformReleaseDate result = GameMappers.PLATFORM_RELEASE_DATE_MAPPER.platformReleaseDateDtoToPlatformReleaseDate(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void platformReleaseDateDtoToPlatformReleaseDate_withPlatformReleaseDateDto_mapsFields() {
        // Arrange
        PlatformReleaseDateDto platformReleaseDateDto = new PlatformReleaseDateDto();
        platformReleaseDateDto.setRegion(GameRegion.JAPAN);
        platformReleaseDateDto.setReleaseDate(LocalDate.now());
        platformReleaseDateDto.setCreatedAt(LocalDateTime.now());
        platformReleaseDateDto.setUpdatedAt(LocalDateTime.now());
        platformReleaseDateDto.setVersion(2L);

        // Act
        PlatformReleaseDate result = GameMappers.PLATFORM_RELEASE_DATE_MAPPER.platformReleaseDateDtoToPlatformReleaseDate(platformReleaseDateDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(platformReleaseDateDto.getId());
        Assertions.assertThat(result.getRegion()).isEqualTo(platformReleaseDateDto.getRegion());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(platformReleaseDateDto.getVersion());
    }
}
