package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameReleaseDateMapper {

    GameReleaseDateDto gameReleaseDateToGameReleaseDateDto(GameReleaseDate gameReleaseDate);

    GameReleaseDate gameReleaseDateDtoToGameReleaseDate(GameReleaseDateDto gameReleaseDateDto);
}
