package com.sparkystudios.traklibrary.game.service.mapper;

import com.github.slugify.Slugify;
import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.service.dto.request.NewGameRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses ={
        AgeRatingMapper.class,
        GameReleaseDateMapper.class,
        DownloadableContentMapper.class
}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface NewGameRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "developers", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "platforms", ignore = true)
    @Mapping(target = "publishers", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "franchise", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "images", ignore = true)
    Game toGame(NewGameRequest newGameRequest);

    @AfterMapping
    default void afterMapping(@MappingTarget Game game) {
        game.setSlug(new Slugify().slugify(game.getTitle()));
    }
}
