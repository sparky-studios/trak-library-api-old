package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.PlatformReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.PlatformReleaseDateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlatformReleaseDateMapper {

    PlatformReleaseDateDto platformReleaseDateToPlatformReleaseDateDto(PlatformReleaseDate platformReleaseDate);

    PlatformReleaseDate platformReleaseDateDtoToPlatformReleaseDate(PlatformReleaseDateDto platformReleaseDateDto);
}
