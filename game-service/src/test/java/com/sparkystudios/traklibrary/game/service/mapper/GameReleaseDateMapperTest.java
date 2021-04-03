package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameRegion;
import com.sparkystudios.traklibrary.game.domain.GameReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
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
        GameReleaseDateMapperImpl.class,
})
class GameReleaseDateMapperTest {

    @Autowired
    private GameReleaseDateMapper gameReleaseDateMapper;

    @Test
    void fromGameReleaseDate_withNull_returnsNull() {
        // Act
        GameReleaseDateDto result = gameReleaseDateMapper.fromGameReleaseDate(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void fromGameReleaseDate_withGameReleaseDate_mapsFields() {
        // Arrange
        GameReleaseDate gameReleaseDate = new GameReleaseDate();
        gameReleaseDate.setRegion(GameRegion.JAPAN);
        gameReleaseDate.setReleaseDate(LocalDate.now());
        gameReleaseDate.setCreatedAt(LocalDateTime.now());
        gameReleaseDate.setUpdatedAt(LocalDateTime.now());
        gameReleaseDate.setVersion(2L);

        // Act
        GameReleaseDateDto result = gameReleaseDateMapper.fromGameReleaseDate(gameReleaseDate);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameReleaseDate.getId());
        Assertions.assertThat(result.getRegion()).isEqualTo(gameReleaseDate.getRegion());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(gameReleaseDate.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(gameReleaseDate.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(gameReleaseDate.getVersion());
    }

    @Test
    void toReleaseDate_withNull_returnsNull() {
        // Act
        GameReleaseDate result = gameReleaseDateMapper.toReleaseDate(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void toReleaseDate_withGameReleaseDateDto_mapsFields() {
        // Arrange
        GameReleaseDateDto gameReleaseDateDto = new GameReleaseDateDto();
        gameReleaseDateDto.setRegion(GameRegion.JAPAN);
        gameReleaseDateDto.setReleaseDate(LocalDate.now());
        gameReleaseDateDto.setVersion(2L);

        // Act
        GameReleaseDate result = gameReleaseDateMapper.toReleaseDate(gameReleaseDateDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameReleaseDateDto.getId());
        Assertions.assertThat(result.getRegion()).isEqualTo(gameReleaseDateDto.getRegion());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(gameReleaseDateDto.getVersion());
    }
}
