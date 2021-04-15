package com.sparkystudios.traklibrary.game.service.mapper;

import com.github.slugify.Slugify;
import com.sparkystudios.traklibrary.game.domain.Developer;
import com.sparkystudios.traklibrary.game.service.dto.DeveloperDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DeveloperMapper {

    DeveloperDto fromDeveloper(Developer developer);

    @Mapping(target = "games", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Developer toDeveloper(DeveloperDto developerDto);

    @AfterMapping
    default void afterMapping(@MappingTarget Developer developer) {
        developer.setSlug(new Slugify().slugify(developer.getName()));
    }
}
