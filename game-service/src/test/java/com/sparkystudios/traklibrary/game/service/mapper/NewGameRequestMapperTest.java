package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.GameMode;
import com.sparkystudios.traklibrary.game.service.dto.AgeRatingDto;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import com.sparkystudios.traklibrary.game.service.dto.request.NewGameRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AgeRatingMapperImpl.class,
        NewGameRequestMapperImpl.class,
        GameReleaseDateMapperImpl.class,
        DownloadableContentMapperImpl.class
})
class NewGameRequestMapperTest {

    @Autowired
    private NewGameRequestMapper newGameRequestMapper;

    @Test
    void toGame_withNull_returnsNull() {
        // Act`
        Game result = newGameRequestMapper.toGame(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void toGame_withNewGameRequest_mapsFields() {
        // Arrange
        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setTitle("Test Title");
        newGameRequest.setDescription("test-description");
        newGameRequest.getGameModes().add(GameMode.MULTI_PLAYER);
        newGameRequest.setFranchiseId(5L);
        newGameRequest.getReleaseDates().add(new GameReleaseDateDto());
        newGameRequest.getAgeRatings().add(new AgeRatingDto());
        newGameRequest.getDownloadableContents().add(new DownloadableContentDto());

        // Act
        Game result = newGameRequestMapper.toGame(newGameRequest);

        // Assert
        Assertions.assertThat(result.getTitle()).isEqualTo(newGameRequest.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(newGameRequest.getDescription());
        Assertions.assertThat(result.getGameModes()).isEqualTo(newGameRequest.getGameModes());
        Assertions.assertThat(result.getFranchiseId()).isEqualTo(newGameRequest.getFranchiseId());
        Assertions.assertThat(result.getSlug()).isEqualTo("test-title");
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isNull();
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
        Assertions.assertThat(result.getAgeRatings()).hasSize(1);
        Assertions.assertThat(result.getDownloadableContents()).hasSize(1);
    }
}
