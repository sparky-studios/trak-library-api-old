package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.GameRequest;
import com.sparky.trak.game.service.dto.GameRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameRequestMapper {

    GameRequestMapper INSTANCE = Mappers.getMapper(GameRequestMapper.class);

    GameRequestDto gameRequestToGameRequestDto(GameRequest gameRequest);

    GameRequest gameRequestDtoToGameRequest(GameRequestDto gameRequestDto);
}
