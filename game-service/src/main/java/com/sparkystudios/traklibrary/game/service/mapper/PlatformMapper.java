package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Platform;
import com.sparkystudios.traklibrary.game.domain.PlatformReleaseDate;
import com.sparkystudios.traklibrary.game.service.dto.PlatformDto;
import com.sparkystudios.traklibrary.game.service.dto.PlatformReleaseDateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlatformMapper {

    PlatformDto platformToPlatformDto(Platform platform);

    @Mapping(target = "games", ignore = true)
    Platform platformDtoToPlatform(PlatformDto platformDto);

    default PlatformReleaseDateDto platformReleaseDateToPlatformReleaseDateDto(PlatformReleaseDate platformReleaseDate) {
        return GameMappers.PLATFORM_RELEASE_DATE_MAPPER.platformReleaseDateToPlatformReleaseDateDto(platformReleaseDate);
    }

    default PlatformReleaseDate gameReleaseDateDtoToGameReleaseDate(PlatformReleaseDateDto platformReleaseDateDto) {
        return GameMappers.PLATFORM_RELEASE_DATE_MAPPER.platformReleaseDateDtoToPlatformReleaseDate(platformReleaseDateDto);
    }
}
