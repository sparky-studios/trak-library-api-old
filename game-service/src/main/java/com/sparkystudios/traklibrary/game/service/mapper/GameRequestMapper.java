package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameRequest;
import com.sparkystudios.traklibrary.game.service.dto.GameRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameRequestMapper {

    GameRequestDto gameRequestToGameRequestDto(GameRequest gameRequest);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    GameRequest gameRequestDtoToGameRequest(GameRequestDto gameRequestDto);
}
