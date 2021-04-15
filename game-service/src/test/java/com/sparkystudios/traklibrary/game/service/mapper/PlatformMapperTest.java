package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameRegion;
import com.sparkystudios.traklibrary.game.domain.Platform;
import com.sparkystudios.traklibrary.game.domain.PlatformReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.PlatformDto;
import com.sparkystudios.traklibrary.game.service.dto.PlatformReleaseDateDto;
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
        PlatformMapperImpl.class,
        PlatformReleaseDateMapperImpl.class
})
class PlatformMapperTest {

    @Autowired
    private PlatformMapper platformMapper;

    @Test
    void fromPlatform_withNull_returnsNull() {
        // Act
        PlatformDto result = platformMapper.fromPlatform(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void fromPlatform_withPlatform_mapsFields() {
        // Arrange
        PlatformReleaseDate platformReleaseDate = new PlatformReleaseDate();
        platformReleaseDate.setRegion(GameRegion.PAL);
        platformReleaseDate.setReleaseDate(LocalDate.now());
        platformReleaseDate.setVersion(1L);

        Platform platform = new Platform();
        platform.setId(5L);
        platform.setName("test-name");
        platform.setDescription("test-description");
        platform.setCreatedAt(LocalDateTime.now());
        platform.setUpdatedAt(LocalDateTime.now());
        platform.setVersion(1L);
        platform.addReleaseDate(platformReleaseDate);

        // Act
        PlatformDto result = platformMapper.fromPlatform(platform);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(platform.getId());
        Assertions.assertThat(result.getName()).isEqualTo(platform.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(platform.getDescription());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(platform.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(platform.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(platform.getVersion());
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
    }

    @Test
    void toPlatform_withNull_returnsNull() {
        // Act
        Platform result = platformMapper.toPlatform(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void toPlatform_withPlatformDto_mapsFields() {
        // Arrange
        PlatformReleaseDateDto platformReleaseDateDto = new PlatformReleaseDateDto();
        platformReleaseDateDto.setRegion(GameRegion.PAL);
        platformReleaseDateDto.setReleaseDate(LocalDate.now());
        platformReleaseDateDto.setVersion(1L);

        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(5L);
        platformDto.setName("Test Name");
        platformDto.setDescription("test-description");
        platformDto.setCreatedAt(LocalDateTime.now());
        platformDto.setUpdatedAt(LocalDateTime.now());
        platformDto.setVersion(1L);
        platformDto.getReleaseDates().add(platformReleaseDateDto);

        // Act
        Platform result = platformMapper.toPlatform(platformDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(platformDto.getId());
        Assertions.assertThat(result.getName()).isEqualTo(platformDto.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(platformDto.getDescription());
        Assertions.assertThat(result.getSlug()).isEqualTo("test-name");
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(platformDto.getVersion());
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
    }
}
