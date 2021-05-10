package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.GameMode;
import com.sparkystudios.traklibrary.game.service.dto.AgeRatingDto;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import com.sparkystudios.traklibrary.game.service.dto.request.UpdateGameRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AgeRatingMapperImpl.class,
        UpdateGameRequestMapperImpl.class,
        GameReleaseDateMapperImpl.class,
        DownloadableContentMapperImpl.class
})
class UpdateGameRequestMapperTest {

    @Autowired
    private UpdateGameRequestMapper updateGameRequestMapper;

    @Test
    void toGame_withNull_returnsNull() {
        // Act`
        Game result = updateGameRequestMapper.toGame(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void toGame_withNewGameRequest_mapsFields() {
        // Arrange
        UpdateGameRequest updateGameRequest = new UpdateGameRequest();
        updateGameRequest.setId(1L);
        updateGameRequest.setTitle("Test Title");
        updateGameRequest.setDescription("test-description");
        updateGameRequest.getGameModes().add(GameMode.MULTI_PLAYER);
        updateGameRequest.setFranchiseId(5L);
        updateGameRequest.getReleaseDates().add(new GameReleaseDateDto());
        updateGameRequest.getAgeRatings().add(new AgeRatingDto());
        updateGameRequest.getDownloadableContents().add(new DownloadableContentDto());
        updateGameRequest.setVersion(2L);

        // Act
        Game result = updateGameRequestMapper.toGame(updateGameRequest);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(updateGameRequest.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(updateGameRequest.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(updateGameRequest.getDescription());
        Assertions.assertThat(result.getGameModes()).isEqualTo(updateGameRequest.getGameModes());
        Assertions.assertThat(result.getFranchiseId()).isEqualTo(updateGameRequest.getFranchiseId());
        Assertions.assertThat(result.getSlug()).isEqualTo("test-title");
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(updateGameRequest.getVersion());
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
        Assertions.assertThat(result.getAgeRatings()).hasSize(1);
        Assertions.assertThat(result.getDownloadableContents()).hasSize(1);
    }
}
