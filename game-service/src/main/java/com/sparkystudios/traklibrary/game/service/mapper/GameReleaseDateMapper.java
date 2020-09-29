package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameReleaseDateMapper {

    GameReleaseDateDto gameReleaseDateToGameReleaseDateDto(GameReleaseDate gameReleaseDate);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    GameReleaseDate gameReleaseDateDtoToGameReleaseDate(GameReleaseDateDto gameReleaseDateDto);
}
