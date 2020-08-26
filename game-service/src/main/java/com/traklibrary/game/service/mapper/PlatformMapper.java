package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.Platform;
import com.traklibrary.game.service.dto.PlatformDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlatformMapper {

    PlatformDto platformToPlatformDto(Platform platform);

    @Mapping(target = "games", ignore = true)
    Platform platformDtoToPlatform(PlatformDto platformDto);
}
