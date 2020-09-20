package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Developer;
import com.sparkystudios.traklibrary.game.service.dto.DeveloperDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeveloperMapper {

    DeveloperDto developerToDeveloperDto(Developer developer);

    @Mapping(target = "games", ignore = true)
    Developer developerDtoToDeveloper(DeveloperDto developerDto);
}
