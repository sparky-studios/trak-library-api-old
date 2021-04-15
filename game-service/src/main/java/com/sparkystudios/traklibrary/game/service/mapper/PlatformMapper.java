package com.sparkystudios.traklibrary.game.service.mapper;

import com.github.slugify.Slugify;
import com.sparkystudios.traklibrary.game.domain.Platform;
import com.sparkystudios.traklibrary.game.service.dto.PlatformDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.TreeSet;

@Mapper(componentModel = "spring", uses = {
        PlatformReleaseDateMapper.class
}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PlatformMapper {

    PlatformDto fromPlatform(Platform platform);

    @Mapping(target = "games", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Platform toPlatform(PlatformDto platformDto);

    @AfterMapping
    default void afterMapping(@MappingTarget PlatformDto platformDto) {
        platformDto.setReleaseDates(new TreeSet<>(platformDto.getReleaseDates()));
    }

    @AfterMapping
    default void afterMapping(@MappingTarget Platform platform) {
        platform.setSlug(new Slugify().slugify(platform.getName()));
    }
}
