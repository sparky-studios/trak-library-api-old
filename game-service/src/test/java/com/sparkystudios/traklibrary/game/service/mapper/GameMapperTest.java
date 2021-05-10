package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.service.dto.AgeRatingDto;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AgeRatingMapperImpl.class,
        GameMapperImpl.class,
        GameReleaseDateMapperImpl.class
})
class GameMapperTest {

    @Autowired
    private GameMapper gameMapper;

    @Test
    void fromGame_withNull_returnsNull() {
        // Act
        GameDto result = gameMapper.fromGame(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void fromGame_withGame_mapsFields() {
        // Arrange
        Game game = new Game();
        game.setId(5L);
        game.setTitle("test-title");
        game.setDescription("test-description");
        game.getGameModes().add(GameMode.MULTI_PLAYER);
        game.setFranchiseId(5L);
        game.setCreatedAt(LocalDateTime.now());
        game.setUpdatedAt(LocalDateTime.now());
        game.setVersion(1L);
        game.addReleaseDate(new GameReleaseDate());
        game.addDownloadableContent(new DownloadableContent());
        game.addAgeRating(new AgeRating());

        // Act
        GameDto result = gameMapper.fromGame(game);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(game.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(game.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(game.getDescription());
        Assertions.assertThat(result.getGameModes()).isEqualTo(game.getGameModes());
        Assertions.assertThat(result.getFranchiseId()).isEqualTo(game.getFranchiseId());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(game.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(game.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(game.getVersion());
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
        Assertions.assertThat(result.getAgeRatings()).hasSize(1);
    }

    @Test
    void toGame_withNull_returnsNull() {
        // Act`
        Game result = gameMapper.toGame(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void toGame_withGameDto_mapsFields() {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("Test Title");
        gameDto.setDescription("test-description");
        gameDto.getGameModes().add(GameMode.MULTI_PLAYER);
        gameDto.setFranchiseId(5L);
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);
        gameDto.getReleaseDates().add(new GameReleaseDateDto());
        gameDto.getAgeRatings().add(new AgeRatingDto());

        // Act
        Game result = gameMapper.toGame(gameDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(gameDto.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(gameDto.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(gameDto.getDescription());
        Assertions.assertThat(result.getGameModes()).isEqualTo(gameDto.getGameModes());
        Assertions.assertThat(result.getFranchiseId()).isEqualTo(gameDto.getFranchiseId());
        Assertions.assertThat(result.getSlug()).isEqualTo("test-title");
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(gameDto.getVersion());
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
        Assertions.assertThat(result.getAgeRatings()).hasSize(1);
    }
}
