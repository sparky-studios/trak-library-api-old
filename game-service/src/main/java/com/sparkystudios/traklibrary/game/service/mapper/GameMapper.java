package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.GameReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameDto gameToGameDto(Game game);

    @Mapping(target = "developers", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "platforms", ignore = true)
    @Mapping(target = "publishers", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Game gameDtoToGame(GameDto gameDto);

    default GameReleaseDateDto gameReleaseDateToGameReleaseDateDto(GameReleaseDate gameReleaseDate) {
        return GameMappers.GAME_RELEASE_DATE_MAPPER.gameReleaseDateToGameReleaseDateDto(gameReleaseDate);
    }

    default GameReleaseDate gameReleaseDateDtoToGameReleaseDate(GameReleaseDateDto gameReleaseDateDto) {
        return GameMappers.GAME_RELEASE_DATE_MAPPER.gameReleaseDateDtoToGameReleaseDate(gameReleaseDateDto);
    }
}
