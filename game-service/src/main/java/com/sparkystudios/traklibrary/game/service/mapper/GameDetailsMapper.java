package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.service.dto.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameDetailsMapper {

    GameDetailsDto gameToGameDetailsDto(Game game);

    default PlatformDto platformToPlatformDto(Platform platform) {
        return GameMappers.PLATFORM_MAPPER.platformToPlatformDto(platform);
    }

    default PublisherDto publisherToPublisherDto(Publisher publisher) {
        return GameMappers.PUBLISHER_MAPPER.publisherToPublisherDto(publisher);
    }

    default GenreDto genreToGenreDto(Genre genre) {
        return GameMappers.GENRE_MAPPER.genreToGenreDto(genre);
    }

    default GameReleaseDateDto gameReleaseDateToGameReleaseDateDto(GameReleaseDate gameReleaseDate) {
        return GameMappers.GAME_RELEASE_DATE_MAPPER.gameReleaseDateToGameReleaseDateDto(gameReleaseDate);
    }

    default FranchiseDto franchiseToFranchiseDto(Franchise franchise) {
        return GameMappers.FRANCHISE_MAPPER.franchiseToFranchiseDto(franchise);
    }
}
