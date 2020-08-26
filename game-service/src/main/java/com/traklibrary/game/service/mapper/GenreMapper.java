package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.Genre;
import com.traklibrary.game.service.dto.GenreDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreDto genreToGenreDto(Genre genre);

    @Mapping(target = "games", ignore = true)
    Genre genreDtoToGenre(GenreDto gameDto);
}
