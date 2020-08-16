package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.GameRequest;
import com.traklibrary.game.service.dto.GameRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameRequestMapper {

    GameRequestDto gameRequestToGameRequestDto(GameRequest gameRequest);

    GameRequest gameRequestDtoToGameRequest(GameRequestDto gameRequestDto);
}
