package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.AgeRating;
import com.sparkystudios.traklibrary.game.domain.DownloadableContent;
import com.sparkystudios.traklibrary.game.domain.Franchise;
import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.GameMode;
import com.sparkystudios.traklibrary.game.domain.GameRegion;
import com.sparkystudios.traklibrary.game.domain.GameReleaseDate;
import com.sparkystudios.traklibrary.game.domain.Genre;
import com.sparkystudios.traklibrary.game.domain.Platform;
import com.sparkystudios.traklibrary.game.domain.Publisher;
import com.sparkystudios.traklibrary.game.service.dto.GameDetailsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AgeRatingMapperImpl.class,
        GameDetailsMapperImpl.class,
        PlatformMapperImpl.class,
        PlatformReleaseDateMapperImpl.class,
        PublisherMapperImpl.class,
        GenreMapperImpl.class,
        GameReleaseDateMapperImpl.class,
        FranchiseMapperImpl.class,
        DownloadableContentMapperImpl.class
})
class GameDetailsMapperTest {

    @Autowired
    private GameDetailsMapper gameDetailsMapper;

    @Test
    void fromGame_withNull_returnsNull() {
        // Act
        GameDetailsDto result = gameDetailsMapper.fromGame(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void fromGame_withGame_mapsFields() {
        // Arrange
        Genre genre = new Genre();
        genre.setName("test-genre");

        Platform platform = new Platform();
        platform.setName("test-platform");

        Publisher publisher = new Publisher();
        publisher.setName("test-publisher");

        GameReleaseDate gameReleaseDate = new GameReleaseDate();
        gameReleaseDate.setRegion(GameRegion.PAL);
        gameReleaseDate.setReleaseDate(LocalDate.now());
        gameReleaseDate.setVersion(1L);

        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setName("test-downloadable-content");
        downloadableContent.setReleaseDate(LocalDate.now());

        Franchise franchise = new Franchise();
        franchise.setTitle("franchise-title");

        Game game = new Game();
        game.setId(5L);
        game.setTitle("test-title");
        game.setDescription("sure is a description.");
        game.getGameModes().add(GameMode.MULTI_PLAYER);
        game.setFranchiseId(5L);
        game.setVersion(5L);
        game.addGenre(genre);
        game.addPlatform(platform);
        game.addPublisher(publisher);
        game.addReleaseDate(gameReleaseDate);
        game.addDownloadableContent(downloadableContent);
        game.setFranchise(franchise);
        game.addAgeRating(new AgeRating());

        // Act
        GameDetailsDto result = gameDetailsMapper.fromGame(game);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(game.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(game.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(game.getDescription());
        Assertions.assertThat(result.getGameModes()).isEqualTo(game.getGameModes());
        Assertions.assertThat(result.getFranchiseId()).isEqualTo(game.getFranchiseId());
        Assertions.assertThat(result.getVersion()).isEqualTo(game.getVersion());
        Assertions.assertThat(result.getGenres()).hasSize(1);
        Assertions.assertThat(result.getPlatforms()).hasSize(1);
        Assertions.assertThat(result.getPublishers()).hasSize(1);
        Assertions.assertThat(result.getReleaseDates()).hasSize(1);
        Assertions.assertThat(result.getDownloadableContents()).hasSize(1);
        Assertions.assertThat(result.getAgeRatings()).hasSize(1);
        Assertions.assertThat(result.getFranchise().getTitle()).isEqualTo(franchise.getTitle());
    }
}
