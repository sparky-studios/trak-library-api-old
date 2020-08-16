package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.Game;
import com.traklibrary.game.service.dto.GameDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameDto gameToGameDto(Game game);

    @Mapping(target = "gamePlatformXrefs", ignore = true)
    @Mapping(target = "gameGenreXrefs", ignore = true)
    @Mapping(target = "gameDeveloperXrefs", ignore = true)
    @Mapping(target = "gamePublisherXrefs", ignore = true)
    Game gameDtoToGame(GameDto gameDto);
}
