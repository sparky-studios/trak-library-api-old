package com.sparkystudios.traklibrary.game.service.mapper;

import com.github.slugify.Slugify;
import com.sparkystudios.traklibrary.game.domain.Genre;
import com.sparkystudios.traklibrary.game.service.dto.GenreDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreDto fromGenre(Genre genre);

    @Mapping(target = "games", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Genre toGenre(GenreDto gameDto);

    @AfterMapping
    default void afterMapping(@MappingTarget Genre genre) {
        genre.setSlug(new Slugify().slugify(genre.getName()));
    }
}
