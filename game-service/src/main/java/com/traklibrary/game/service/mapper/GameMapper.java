package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.Game;
import com.traklibrary.game.service.dto.GameDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameDto gameToGameDto(Game game);

    @Mapping(target = "developers", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "platforms", ignore = true)
    @Mapping(target = "publishers", ignore = true)
    Game gameDtoToGame(GameDto gameDto);
}
