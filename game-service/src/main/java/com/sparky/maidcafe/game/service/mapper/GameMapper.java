package com.sparky.maidcafe.game.service.mapper;

import com.sparky.maidcafe.game.domain.Game;
import com.sparky.maidcafe.game.service.dto.GameDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    GameDto gameToGameDto(Game game);

    Game gameDtoToGame(GameDto gameDto);
}
