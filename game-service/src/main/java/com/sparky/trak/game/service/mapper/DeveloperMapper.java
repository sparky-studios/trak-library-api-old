package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.Developer;
import com.sparky.trak.game.service.dto.DeveloperDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DeveloperMapper {

    DeveloperMapper INSTANCE = Mappers.getMapper(DeveloperMapper.class);

    DeveloperDto developerToDeveloperDto(Developer developer);

    @Mapping(target = "gameDeveloperXrefs", ignore = true)
    Developer developerDtoToDeveloper(DeveloperDto developerDto);
}
