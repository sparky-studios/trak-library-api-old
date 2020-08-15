package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.GameRequest;
import com.traklibrary.game.service.dto.GameRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameRequestMapper {

    GameRequestMapper INSTANCE = Mappers.getMapper(GameRequestMapper.class);

    GameRequestDto gameRequestToGameRequestDto(GameRequest gameRequest);

    GameRequest gameRequestDtoToGameRequest(GameRequestDto gameRequestDto);
}
