package com.sparky.maidcafe.game.service.mapper;

import com.sparky.maidcafe.game.domain.Genre;
import com.sparky.maidcafe.game.service.dto.GenreDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreMapper INSTANCE = Mappers.getMapper(GenreMapper.class);

    GenreDto genreToGenreDto(Genre genre);

    Genre genreDtoToGenre(GenreDto gameDto);
}
