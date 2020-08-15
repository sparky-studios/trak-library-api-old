package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.Game;
import com.traklibrary.game.domain.GameGenreXref;
import com.traklibrary.game.domain.GamePlatformXref;
import com.traklibrary.game.domain.GamePublisherXref;
import com.traklibrary.game.service.dto.GameInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameInfoMapper {

    GameInfoMapper INSTANCE = Mappers.getMapper(GameInfoMapper.class);

    @Mapping(source = "gamePlatformXrefs", target = "platforms")
    @Mapping(source = "gamePublisherXrefs", target = "publishers")
    @Mapping(source = "gameGenreXrefs", target = "genres")
    GameInfoDto gameToGameInfoDto(Game game);

    default String gamePlatform(GamePlatformXref gamePlatformXref) {
        return gamePlatformXref.getPlatform().getName();
    }

    default String gamePublisher(GamePublisherXref gamePublisherXref) {
        return gamePublisherXref.getPublisher().getName();
    }

    default String gameGenre(GameGenreXref gameGenreXref) {
        return gameGenreXref.getGenre().getName();
    }
}
