package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.GameUserEntry;
import com.sparkystudios.traklibrary.game.domain.GameUserEntryDownloadableContent;
import com.sparkystudios.traklibrary.game.domain.GameUserEntryPlatform;
import com.sparkystudios.traklibrary.game.domain.Publisher;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDownloadableContentDto;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryPlatformDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameUserEntryMapper {

    @Mapping(source = "game.title", target = "gameTitle")
    @Mapping(source = "game.publishers", target = "publishers")
    GameUserEntryDto gameUserEntryToGameUserEntryDto(GameUserEntry gameUserEntry);

    default String publisherToPublisherName(Publisher publisher) {
        return publisher.getName();
    }

    default GameUserEntryPlatformDto gameUserEntryPlatformToGameUserEntryPlatformDto(GameUserEntryPlatform gameUserEntryPlatform) {
        return GameMappers.GAME_USER_ENTRY_PLATFORM_MAPPER.gameUserEntryPlatformToGameUserEntryPlatformDto(gameUserEntryPlatform);
    }

    default GameUserEntryDownloadableContentDto gameUserEntryDownloadableContentToGameUserEntryDownloadableContentDto(GameUserEntryDownloadableContent gameUserEntryDownloadableContent) {
        return GameMappers.GAME_USER_ENTRY_DOWNLOADABLE_CONTENT_MAPPER.gameUserEntryDownloadableContentToGameUserEntryDownloadableContentDto(gameUserEntryDownloadableContent);
    }
}
