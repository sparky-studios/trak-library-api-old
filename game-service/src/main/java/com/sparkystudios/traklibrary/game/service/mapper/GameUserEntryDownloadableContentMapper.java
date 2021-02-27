package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameUserEntryDownloadableContent;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDownloadableContentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameUserEntryDownloadableContentMapper {

    @Mapping(source = "downloadableContent.name", target = "downloadableContentName")
    GameUserEntryDownloadableContentDto gameUserEntryDownloadableContentToGameUserEntryDownloadableContentDto(GameUserEntryDownloadableContent gameUserEntryDownloadableContent);
}
