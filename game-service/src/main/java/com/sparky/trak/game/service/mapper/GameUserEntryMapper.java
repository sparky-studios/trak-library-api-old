package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.GameUserEntry;
import com.sparky.trak.game.service.dto.GameUserEntryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameUserEntryMapper {

    GameUserEntryMapper INSTANCE = Mappers.getMapper(GameUserEntryMapper.class);

    @Mapping(source = "game.title", target = "gameTitle")
    @Mapping(source = "console.name", target = "consoleName")
    GameUserEntryDto gameUserEntryToGameUserEntryDto(GameUserEntry gameUserEntry);

    @Mapping(target = "game", ignore = true)
    @Mapping(target = "console", ignore = true)
    GameUserEntry gameUserEntryDtoToGameUserEntry(GameUserEntryDto gameUserEntryDto);
}
