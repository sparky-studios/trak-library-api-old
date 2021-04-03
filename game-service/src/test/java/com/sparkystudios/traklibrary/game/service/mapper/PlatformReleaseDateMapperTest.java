package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameRegion;
import com.sparkystudios.traklibrary.game.domain.PlatformReleaseDate;
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
        PlatformReleaseDateMapperImpl.class,
})
class PlatformReleaseDateMapperTest {

    @Autowired
    private PlatformReleaseDateMapper platformReleaseDateMapper;

    @Test
    void fromPlatformReleaseDate_withNull_returnsNull() {
        // Act
        PlatformReleaseDateDto result = platformReleaseDateMapper.fromPlatformReleaseDate(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void fromPlatformReleaseDate_withPlatformReleaseDate_mapsFields() {
        // Arrange
        PlatformReleaseDate platformReleaseDate = new PlatformReleaseDate();
        platformReleaseDate.setRegion(GameRegion.JAPAN);
        platformReleaseDate.setReleaseDate(LocalDate.now());
        platformReleaseDate.setCreatedAt(LocalDateTime.now());
        platformReleaseDate.setUpdatedAt(LocalDateTime.now());
        platformReleaseDate.setVersion(2L);

        // Act
        PlatformReleaseDateDto result = platformReleaseDateMapper.fromPlatformReleaseDate(platformReleaseDate);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(platformReleaseDate.getId());
        Assertions.assertThat(result.getRegion()).isEqualTo(platformReleaseDate.getRegion());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(platformReleaseDate.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(platformReleaseDate.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(platformReleaseDate.getVersion());
    }

    @Test
    void toPlatformReleaseDate_withNull_returnsNull() {
        // Act
        PlatformReleaseDate result = platformReleaseDateMapper.toPlatformReleaseDate(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void toPlatformReleaseDate_withPlatformReleaseDateDto_mapsFields() {
        // Arrange
        PlatformReleaseDateDto platformReleaseDateDto = new PlatformReleaseDateDto();
        platformReleaseDateDto.setRegion(GameRegion.JAPAN);
        platformReleaseDateDto.setReleaseDate(LocalDate.now());
        platformReleaseDateDto.setCreatedAt(LocalDateTime.now());
        platformReleaseDateDto.setUpdatedAt(LocalDateTime.now());
        platformReleaseDateDto.setVersion(2L);

        // Act
        PlatformReleaseDate result = platformReleaseDateMapper.toPlatformReleaseDate(platformReleaseDateDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(platformReleaseDateDto.getId());
        Assertions.assertThat(result.getRegion()).isEqualTo(platformReleaseDateDto.getRegion());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(platformReleaseDateDto.getVersion());
    }
}
