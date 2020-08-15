package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.Platform;
import com.traklibrary.game.service.dto.PlatformDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PlatformMapper {

    PlatformMapper INSTANCE = Mappers.getMapper(PlatformMapper.class);

    PlatformDto platformToPlatformDto(Platform platform);

    @Mapping(target = "gamePlatformXrefs", ignore = true)
    Platform platformDtoToPlatform(PlatformDto platformDto);
}
