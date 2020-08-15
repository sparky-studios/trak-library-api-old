package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.Genre;
import com.traklibrary.game.service.dto.GenreDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreMapper INSTANCE = Mappers.getMapper(GenreMapper.class);

    GenreDto genreToGenreDto(Genre genre);

    @Mapping(target = "gameGenreXrefs", ignore = true)
    Genre genreDtoToGenre(GenreDto gameDto);
}
