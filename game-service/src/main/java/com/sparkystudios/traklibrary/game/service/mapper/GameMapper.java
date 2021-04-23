package com.sparkystudios.traklibrary.game.service.mapper;

import com.github.slugify.Slugify;
import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.TreeSet;

@Mapper(componentModel = "spring", uses ={
        GameReleaseDateMapper.class,
        DownloadableContentMapper.class
}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GameMapper {

    GameDto fromGame(Game game);

    @Mapping(target = "developers", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "platforms", ignore = true)
    @Mapping(target = "publishers", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "franchise", ignore = true)
    Game toGame(GameDto gameDto);

    @AfterMapping
    default void afterMapping(@MappingTarget GameDto gameDto) {
        gameDto.setReleaseDates(new TreeSet<>(gameDto.getReleaseDates()));
        gameDto.setDownloadableContents(new TreeSet<>(gameDto.getDownloadableContents()));
    }

    @AfterMapping
    default void afterMapping(@MappingTarget Game game) {
        game.setSlug(new Slugify().slugify(game.getTitle()));
    }
}
