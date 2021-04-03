package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameUserEntry;
import com.sparkystudios.traklibrary.game.domain.Publisher;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.TreeSet;

@Mapper(componentModel = "spring", uses = {
        GameUserEntryPlatformMapper.class,
        GameUserEntryDownloadableContentMapper.class
}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GameUserEntryMapper {

    @Mapping(source = "game.title", target = "gameTitle")
    @Mapping(source = "game.publishers", target = "publishers")
    GameUserEntryDto fromGameUserEntry(GameUserEntry gameUserEntry);

    default String publisherToPublisherName(Publisher publisher) {
        return publisher.getName();
    }

    @AfterMapping
    default void afterMapping(@MappingTarget GameUserEntryDto gameUserEntryDto) {
        gameUserEntryDto.setGameUserEntryPlatforms(new TreeSet<>(gameUserEntryDto.getGameUserEntryPlatforms()));
        gameUserEntryDto.setGameUserEntryDownloadableContents(new TreeSet<>(gameUserEntryDto.getGameUserEntryDownloadableContents()));
    }
}
