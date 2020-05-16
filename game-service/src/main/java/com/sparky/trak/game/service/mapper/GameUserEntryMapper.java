package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.GamePublisherXref;
import com.sparky.trak.game.domain.GameUserEntry;
import com.sparky.trak.game.service.dto.GameUserEntryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface GameUserEntryMapper {

    GameUserEntryMapper INSTANCE = Mappers.getMapper(GameUserEntryMapper.class);

    @Mapping(source = "game.title", target = "gameTitle")
    @Mapping(source = "game.releaseDate", target = "gameReleaseDate")
    @Mapping(source = "platform.name", target = "platformName")
    @Mapping(source = "game.gamePublisherXrefs", target = "publishers")
    GameUserEntryDto gameUserEntryToGameUserEntryDto(GameUserEntry gameUserEntry);

    @Mapping(target = "game", ignore = true)
    @Mapping(target = "platform", ignore = true)
    GameUserEntry gameUserEntryDtoToGameUserEntry(GameUserEntryDto gameUserEntryDto);

    default String gameUserEntryPublisher(GamePublisherXref gamePublisherXref) {
        return gamePublisherXref.getPublisher().getName();
    }
}
