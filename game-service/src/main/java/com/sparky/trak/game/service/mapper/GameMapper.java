package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.Game;
import com.sparky.trak.game.service.dto.GameDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    GameDto gameToGameDto(Game game);

    @Mapping(target = "gamePlatformXrefs", ignore = true)
    @Mapping(target = "gameGenreXrefs", ignore = true)
    @Mapping(target = "gameDeveloperXrefs", ignore = true)
    @Mapping(target = "gamePublisherXrefs", ignore = true)
    Game gameDtoToGame(GameDto gameDto);
}
