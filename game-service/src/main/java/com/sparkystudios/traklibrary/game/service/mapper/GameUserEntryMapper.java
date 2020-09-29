package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameUserEntry;
import com.sparkystudios.traklibrary.game.domain.Publisher;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameUserEntryMapper {

    @Mapping(source = "game.title", target = "gameTitle")
    @Mapping(source = "platform.name", target = "platformName")
    @Mapping(source = "game.publishers", target = "publishers")
    GameUserEntryDto gameUserEntryToGameUserEntryDto(GameUserEntry gameUserEntry);

    @Mapping(target = "game", ignore = true)
    @Mapping(target = "platform", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    GameUserEntry gameUserEntryDtoToGameUserEntry(GameUserEntryDto gameUserEntryDto);

    default String publisherToPublisherName(Publisher publisher) {
        return publisher.getName();
    }
}
