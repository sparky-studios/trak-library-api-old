package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Genre;
import com.sparkystudios.traklibrary.game.service.dto.GenreDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreDto fromGenre(Genre genre);

    @Mapping(target = "games", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Genre toGenre(GenreDto gameDto);
}
