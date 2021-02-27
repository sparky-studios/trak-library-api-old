package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.DownloadableContent;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DownloadableContentMapper {

    DownloadableContentDto downloadableContentToDownloadableContentDto(DownloadableContent downloadableContent);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "game", ignore = true)
    DownloadableContent downloadableContentDtoToDownloadableContent(DownloadableContentDto downloadableContentDto);
}
