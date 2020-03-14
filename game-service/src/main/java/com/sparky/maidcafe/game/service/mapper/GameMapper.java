package com.sparky.maidcafe.game.service.mapper;

import com.sparky.maidcafe.game.domain.Game;
import com.sparky.maidcafe.game.service.dto.GameDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    GameDto gameToGameDto(Game game);

    @Mapping(target = "gameConsoleXrefs", ignore = true)
    @Mapping(target = "gameGenreXrefs", ignore = true)
    Game gameDtoToGame(GameDto gameDto);
}
