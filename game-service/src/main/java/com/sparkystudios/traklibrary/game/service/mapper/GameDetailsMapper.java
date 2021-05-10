package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.service.dto.GameDetailsDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.TreeSet;

@Mapper(componentModel = "spring", uses = {
        PlatformMapper.class,
        PublisherMapper.class,
        GenreMapper.class,
        GameReleaseDateMapper.class,
        FranchiseMapper.class
}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GameDetailsMapper {

    GameDetailsDto fromGame(Game game);

    @AfterMapping
    default void afterMapping(@MappingTarget GameDetailsDto gameDetailsDto) {
        gameDetailsDto.setAgeRatings(new TreeSet<>(gameDetailsDto.getAgeRatings()));
        gameDetailsDto.setPlatforms(new TreeSet<>(gameDetailsDto.getPlatforms()));
        gameDetailsDto.setPublishers(new TreeSet<>(gameDetailsDto.getPublishers()));
        gameDetailsDto.setGenres(new TreeSet<>(gameDetailsDto.getGenres()));
        gameDetailsDto.setReleaseDates(new TreeSet<>(gameDetailsDto.getReleaseDates()));
    }
}
