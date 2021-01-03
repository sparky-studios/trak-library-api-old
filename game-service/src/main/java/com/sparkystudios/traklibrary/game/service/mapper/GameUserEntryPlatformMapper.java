package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameUserEntryPlatform;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryPlatformDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameUserEntryPlatformMapper {

    @Mapping(source = "platform.name", target = "platformName")
    GameUserEntryPlatformDto gameUserEntryPlatformToGameUserEntryPlatformDto(GameUserEntryPlatform gameUserEntryPlatform);
}
