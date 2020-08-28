package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.GameUserEntry;
import com.traklibrary.game.domain.Publisher;
import com.traklibrary.game.service.dto.GameUserEntryDto;
import com.traklibrary.game.service.dto.PublisherDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameUserEntryMapper {

    @Mapping(source = "game.title", target = "gameTitle")
    @Mapping(source = "game.releaseDate", target = "gameReleaseDate")
    @Mapping(source = "platform.name", target = "platformName")
    @Mapping(source = "game.publishers", target = "publishers")
    GameUserEntryDto gameUserEntryToGameUserEntryDto(GameUserEntry gameUserEntry);

    @Mapping(target = "game", ignore = true)
    @Mapping(target = "platform", ignore = true)
    GameUserEntry gameUserEntryDtoToGameUserEntry(GameUserEntryDto gameUserEntryDto);

    default String publisherToPublisherName(Publisher publisher) {
        return publisher.getName();
    }
}
