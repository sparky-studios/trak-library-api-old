package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.Genre;
import com.sparkystudios.traklibrary.game.domain.Platform;
import com.sparkystudios.traklibrary.game.domain.Publisher;
import com.sparkystudios.traklibrary.game.service.dto.GameInfoDto;
import com.sparkystudios.traklibrary.game.service.dto.GenreDto;
import com.sparkystudios.traklibrary.game.service.dto.PlatformDto;
import com.sparkystudios.traklibrary.game.service.dto.PublisherDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameInfoMapper {

    GameInfoDto gameToGameInfoDto(Game game);

    default PlatformDto platformToPlatformDto(Platform platform) {
        return GameMappers.PLATFORM_MAPPER.platformToPlatformDto(platform);
    }

    default PublisherDto publisherToPublisherDto(Publisher publisher) {
        return GameMappers.PUBLISHER_MAPPER.publisherToPublisherDto(publisher);
    }

    default GenreDto genreToGenreDto(Genre genre) {
        return GameMappers.GENRE_MAPPER.genreToGenreDto(genre);
    }
}
