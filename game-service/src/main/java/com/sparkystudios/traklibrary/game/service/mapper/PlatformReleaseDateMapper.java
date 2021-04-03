package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.PlatformReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.PlatformReleaseDateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlatformReleaseDateMapper {

    PlatformReleaseDateDto fromPlatformReleaseDate(PlatformReleaseDate platformReleaseDate);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "platform", ignore = true)
    PlatformReleaseDate toPlatformReleaseDate(PlatformReleaseDateDto platformReleaseDateDto);
}
